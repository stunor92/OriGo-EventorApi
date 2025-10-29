package no.stunor.origo.eventorapi.services.converter
import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.data.RegionRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.calendar.*
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.entry.Result
import no.stunor.origo.eventorapi.model.event.entry.ResultStatus
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.person.Person
import org.springframework.stereotype.Component
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat


@Component
class CalendarConverter(
    var organisationRepository: OrganisationRepository,
    var regionRepository: RegionRepository
) {
    var eventConverter = EventConverter()

    var organisationConverter = OrganisationConverter(
        organisationRepository = organisationRepository,
        regionRepository = regionRepository
    )
    var entryListConverter = EntryListConverter()

    // Helper data holders to reduce long parameter lists
    private data class CompetitorContext(val eventor: Eventor, val eventId: String, val person: Person)
    private data class CompetitorData(
        val entry: org.iof.eventor.Entry? = null,
        val classStart: org.iof.eventor.ClassStart? = null,
        val start: Any? = null,
        val classResult: org.iof.eventor.ClassResult? = null,
        val result: Any? = null,
        val eventClassList: org.iof.eventor.EventClassList? = null
    )

    /**
     * Convert an Eventor EventList into a list of CalendarRace domain objects.
     * Duplicates are not expected; races are generated per EventRace contained in each Event.
     * @param eventList Source list from Eventor (nullable – returns empty list if null)
     * @param eventor Context describing the Eventor instance/environment
     * @param competitorCountList Counts used for entries / organisation entries
     */
    fun convertEvents(
        eventList: org.iof.eventor.EventList?,
        eventor: Eventor,
        competitorCountList: org.iof.eventor.CompetitorCountList
    ): List<CalendarRace> = eventList?.event?.flatMap { convertEvent(it, eventor, competitorCountList) } ?: emptyList()

    private fun convertEvent(
        event: org.iof.eventor.Event,
        eventor: Eventor,
        competitorCountList: org.iof.eventor.CompetitorCountList
    ): List<CalendarRace> = event.eventRace.map { generateRace(event, it, eventor, competitorCountList) }

    private fun generateRace(
        event: org.iof.eventor.Event,
        eventRace: org.iof.eventor.EventRace,
        eventor: Eventor,
        competitorCountList: org.iof.eventor.CompetitorCountList
    ): CalendarRace = CalendarRace(
        eventor = eventor,
        eventId = event.eventId.content,
        eventName = event.name.content,
        raceId = eventRace.eventRaceId.content,
        raceName = eventRace.name.content,
        raceDate = TimeStampConverter.parseDate("${eventRace.raceDate.date.content} 00:00:00"),
        type = eventConverter.convertEventForm(event.eventForm),
        classification = eventConverter.convertEventClassification(event.eventClassificationId.content),
        lightCondition = eventConverter.convertLightCondition(eventRace.raceLightCondition),
        distance = eventConverter.convertRaceDistance(eventRace.raceDistance),
        position = eventRace.eventCenterPosition?.let { eventConverter.convertPosition(it) },
        status = eventConverter.convertEventStatus(event.eventStatusId.content),
        disciplines = eventConverter.convertEventDisciplines(event.disciplineIdOrDiscipline),
        organisers = event.organiser?.let { organisationConverter.convertOrganisations(it.organisationIdOrOrganisation, eventor.id) } ?: listOf(),
        entryBreaks = eventConverter.convertEntryBreaks(event.entryBreak, eventor),
        entries = getEntries(event.eventId.content, eventRace.eventRaceId.content, competitorCountList),
        userEntries = mutableListOf(),
        organisationEntries = getOrganisationEntries(event.eventId.content, eventRace.eventRaceId.content, competitorCountList, eventor),
        signedUp = isSignedUp(event.eventId.content, competitorCountList),
        startList = eventConverter.hasStartList(event.hashTableEntry, eventRace.eventRaceId.content),
        resultList = eventConverter.hasResultList(event.hashTableEntry, eventRace.eventRaceId.content),
        livelox = eventConverter.hasLivelox(event.hashTableEntry)
    )

    private fun getEntries(eventId: String, eventRaceId: String, competitorCountList: org.iof.eventor.CompetitorCountList?): Int =
        competitorCountList?.competitorCount?.firstOrNull { it.eventId == eventId && (it.eventRaceId == null || it.eventRaceId == eventRaceId) }?.numberOfEntries?.toInt()
            ?: 0

    private fun getOrganisationEntries(
        eventId: String,
        eventRaceId: String,
        competitorCountList: org.iof.eventor.CompetitorCountList,
        eventor: Eventor
    ): MutableList<OrganisationEntries> = competitorCountList.competitorCount
        .filter { isRelevantCompetitorCount(it, eventId, eventRaceId) }
        .flatMap { it.organisationCompetitorCount ?: emptyList() }
        .mapNotNull { occ -> organisationConverter.convertOrganisation(occ.organisationId, eventor.id)?.let { OrganisationEntries(it, occ.numberOfEntries.toInt()) } }
        .toMutableList()

    private fun isRelevantCompetitorCount(competitorCount: org.iof.eventor.CompetitorCount, eventId: String, eventRaceId: String): Boolean =
        competitorCount.eventId == eventId && (competitorCount.eventRaceId == null || competitorCount.eventRaceId == eventRaceId) && competitorCount.organisationCompetitorCount != null

    private fun isSignedUp(eventId: String, competitorCountList: org.iof.eventor.CompetitorCountList?): Boolean =
        competitorCountList?.competitorCount?.any { it.eventId == eventId && !it.classCompetitorCount.isNullOrEmpty() } ?: false

    /**
     * Convert an EntryList for a specific person, producing/augmenting a race map keyed by raceId.
     * User entries (person's own entries) and organisation entry counts are populated.
     * @param eventor Context
     * @param entryList Entry list from Eventor (nullable)
     * @param person Person for which we extract userEntries
     * @param eventClassMap Mapping raceId -> EventClassList to resolve event classes for entries
     * @return Mutable race map (created if empty or updated if provided)
     */
    fun convertEntryList(
        eventor: Eventor,
        entryList: org.iof.eventor.EntryList?,
        person: Person,
        eventClassMap: Map<String, org.iof.eventor.EventClassList>
    ): MutableMap<String?, CalendarRace> {
        val raceMap = mutableMapOf<String?, CalendarRace>()
        entryList?.entry?.forEach { entry ->
            entry.eventRaceId.forEach { raceIdObj ->
                val raceId = raceIdObj.content
                val raceObj = entry.event.eventRace.find { it.eventRaceId.content == raceId } ?: return@forEach
                val race = raceMap.getOrPut(raceId) { generateRace(entry.event, raceObj, eventor, org.iof.eventor.CompetitorCountList()) }

                organisationConverter.convertOrganisation(entry.competitor.organisationId.content, eventor.id)?.let { org ->
                    race.organisationEntries = updateOrganisationEntries(race.organisationEntries, org)
                }
                if (entry.competitor.personId.content == person.eventorRef) {
                    val context = CompetitorContext(eventor, entry.event.eventId.content, person)
                    val data = CompetitorData(entry = entry, eventClassList = eventClassMap[raceId])
                    race.userEntries.add(assembleCompetitor(null, context, data))
                }
                race.signedUp = race.userEntries.isNotEmpty()
            }
        }
        return raceMap
    }

    private fun updateOrganisationEntries(list: MutableList<OrganisationEntries>, organisation: Organisation): MutableList<OrganisationEntries> {
        list.find { it.organisation == organisation }?.let { it.entries++ } ?: list.add(OrganisationEntries(organisation, 1))
        return list
    }

    // ----------------------------------------------------------------------------------
    // Refactored helper methods for start/result list conversion
    // ----------------------------------------------------------------------------------

    private fun getOrCreateRace(
        raceMap: MutableMap<String?, CalendarRace>,
        raceId: String,
        event: org.iof.eventor.Event,
        eventRace: org.iof.eventor.EventRace,
        eventor: Eventor
    ) = raceMap.getOrPut(raceId) { generateRace(event, eventRace, eventor, org.iof.eventor.CompetitorCountList()) }

    private fun findRaceById(event: org.iof.eventor.Event, raceId: String): org.iof.eventor.EventRace? = event.eventRace.find { it.eventRaceId.content == raceId }

    private fun replaceOrAddUserEntry(race: CalendarRace, competitor: CalendarCompetitor) {
        if (race.userEntries.isEmpty()) race.userEntries.add(competitor) else race.userEntries[0] = competitor
        race.signedUp = true
    }

    /**
     * Merge StartList information for a person into an existing race map.
     * Handles both single-race and multi-race events; for multi-race events only PersonStart is processed.
     * @param eventor Context
     * @param startListList Start lists from Eventor (nullable)
     * @param person Target person
     * @param raceMap Existing race map to enrich
     * @return Updated race map
     */
    fun convertStartListList(
        eventor: Eventor,
        startListList: org.iof.eventor.StartListList?,
        person: Person,
        raceMap: MutableMap<String?, CalendarRace>
    ): MutableMap<String?, CalendarRace> {
        startListList?.startList?.forEach { processStartList(it, eventor, person, raceMap) }
        return raceMap
    }

    private fun processStartList(
        startList: org.iof.eventor.StartList,
        eventor: Eventor,
        person: Person,
        raceMap: MutableMap<String?, CalendarRace>
    ) {
        val singleRace = startList.event.eventRace.size == 1
        startList.classStart.forEach { classStart ->
            classStart.personStartOrTeamStart.forEach { start ->
                if (!singleRace && start !is org.iof.eventor.PersonStart) return@forEach
                val raceId = if (singleRace) startList.event.eventRace[0].eventRaceId.content else (start as org.iof.eventor.PersonStart).raceStart[0].eventRaceId.content
                val data = CompetitorData(classStart = classStart, start = start)
                updateRaceWithData(startList.event, raceId, eventor, person, raceMap, data)
            }
        }
    }

    /**
     * Merge ResultList information for a person into an existing race map.
     * In multi-race events only person results are considered (team results ignored to match previous logic).
     * @param eventor Context
     * @param resultListList Result lists from Eventor (nullable)
     * @param person Target person
     * @param raceMap Existing race map to enrich
     * @return Updated race map
     */
    fun convertResultList(
        eventor: Eventor,
        resultListList: org.iof.eventor.ResultListList?,
        person: Person,
        raceMap: MutableMap<String?, CalendarRace>
    ): MutableMap<String?, CalendarRace> {
        resultListList?.resultList?.forEach { processResultList(it, eventor, person, raceMap) }
        return raceMap
    }

    private fun processResultList(
        resultList: org.iof.eventor.ResultList,
        eventor: Eventor,
        person: Person,
        raceMap: MutableMap<String?, CalendarRace>
    ) {
        val singleRace = resultList.event.eventRace.size == 1
        resultList.classResult.forEach { classResult ->
            classResult.personResultOrTeamResult.forEach { anyResult ->
                if (!singleRace && anyResult !is org.iof.eventor.PersonResult) return@forEach
                val raceId = if (singleRace) resultList.event.eventRace[0].eventRaceId.content else (anyResult as org.iof.eventor.PersonResult).raceResult[0].eventRaceId.content
                val data = CompetitorData(classResult = classResult, result = anyResult)
                updateRaceWithData(resultList.event, raceId, eventor, person, raceMap, data)
            }
        }
    }

    private fun updateRaceWithData(
        event: org.iof.eventor.Event,
        raceId: String,
        eventor: Eventor,
        person: Person,
        raceMap: MutableMap<String?, CalendarRace>,
        data: CompetitorData
    ) {
        val raceObj = findRaceById(event, raceId) ?: return
        val race = getOrCreateRace(raceMap, raceId, event, raceObj, eventor)
        val context = CompetitorContext(eventor, event.eventId.content, person)
        val competitor = assembleCompetitor(race.userEntries.firstOrNull(), context, data)
        replaceOrAddUserEntry(race, competitor)
    }

    private fun assembleCompetitor(existing: CalendarCompetitor?, context: CompetitorContext, data: CompetitorData): CalendarCompetitor {
        val newEntry = existing?.personEntry ?: data.entry?.let { createUserEntry(it, data.eventClassList, context.eventId, context.eventor) }
        val newPersonStart = when {
            data.start is org.iof.eventor.PersonStart && data.classStart != null -> createPersonStart(context.eventor, context.eventId, data.start, data.classStart)
            else -> existing?.personStart
        }
        val newTeamStart = when {
            data.start is org.iof.eventor.TeamStart && data.classStart != null -> createTeamStart(context.eventor, context.eventId, data.start, data.classStart)
            else -> existing?.teamStart
        }
        val newPersonResult = when {
            data.result is org.iof.eventor.PersonResult && data.classResult != null -> createPersonResult(context.eventor, context.eventId, data.result, data.classResult) ?: existing?.personResult
            else -> existing?.personResult
        }
        val newTeamResult = when {
            data.result is org.iof.eventor.TeamResult && data.classResult != null -> createTeamResult(context.eventor, context.eventId, data.result, data.classResult)
            else -> existing?.teamResult
        }
        return CalendarCompetitor(
            personId = context.person.eventorRef,
            name = context.person.name,
            personEntry = newEntry,
            personStart = newPersonStart,
            teamStart = newTeamStart,
            personResult = newPersonResult,
            teamResult = newTeamResult
        )
    }

    private fun createUserEntry(
        entry: org.iof.eventor.Entry,
        eventClassList: org.iof.eventor.EventClassList?,
        eventId: String,
        eventor: Eventor
    ): CalendarEntry = CalendarEntry(
        eventClass = if (!entry.entryClass.isNullOrEmpty()) EventClassConverter.getEventClassFromId(
            eventClassList = eventClassList!!,
            entryClassId = entry.entryClass[0].eventClassId.content,
            event = Event(eventorId = eventor.id, eventorRef = eventId),
        ) else null,
        punchingUnits = entryListConverter.convertPunchingUnits(entry.competitor.cCard),
    )

    private fun createPersonStart(
        eventor: Eventor,
        eventId: String,
        personStart: org.iof.eventor.PersonStart,
        classStart: org.iof.eventor.ClassStart
    ): CalendarPersonStart {
        val start: org.iof.eventor.Start = personStart.start ?: personStart.raceStart[0].start
        return CalendarPersonStart(
            startTime = start.startTime?.let { TimeStampConverter.parseDate("${it.date.content} ${it.clock.content}", eventor.id) },
            bib = start.bibNumber?.content,
            eventClass = EventClassConverter.convertEventClass(Event(eventorId = eventor.id, eventorRef = eventId), classStart.eventClass)
        )
    }

    private fun createTeamStart(
        eventor: Eventor,
        eventId: String,
        teamStart: org.iof.eventor.TeamStart,
        classStart: org.iof.eventor.ClassStart
    ): CalendarTeamStart = CalendarTeamStart(
        teamName = teamStart.teamName.content,
        startTime = teamStart.startTime?.let { TimeStampConverter.parseDate("${it.date.content} ${it.clock.content}", eventor.id) },
        bib = teamStart.bibNumber?.content,
        leg = teamStart.teamMemberStart[0].leg.toInt(),
        eventClass = EventClassConverter.convertEventClass(Event(eventorId = eventor.id, eventorRef = eventId), classStart.eventClass)
    )

    private fun createPersonResult(
        eventor: Eventor,
        eventId: String,
        personResult: org.iof.eventor.PersonResult,
        classResult: org.iof.eventor.ClassResult
    ): CalendarPersonResult? {
        val result: org.iof.eventor.Result? = when {
            personResult.result != null && personResult.result.competitorStatus.value != "Inactive" -> personResult.result
            !personResult.raceResult.isNullOrEmpty() && personResult.raceResult[0].result.competitorStatus.value != "Inactive" -> personResult.raceResult[0].result
            else -> null
        }
        return result?.let { r ->
            CalendarPersonResult(
                result = Result(
                    time = r.time?.content?.let { convertTimeSec(it) },
                    timeBehind = r.timeDiff?.content?.let { convertTimeSec(it) },
                    position = r.resultPosition?.content?.takeIf { it != "0" }?.toInt(),
                    status = ResultStatus.valueOf(r.competitorStatus.value),
                ),
                bib = r.bibNumber?.content,
                eventClass = EventClassConverter.convertEventClass(Event(eventorId = eventor.id, eventorRef = eventId), classResult.eventClass)
            )
        }
    }

    private fun createTeamResult(
        eventor: Eventor,
        eventId: String,
        teamResult: org.iof.eventor.TeamResult,
        classResult: org.iof.eventor.ClassResult
    ): CalendarTeamResult = CalendarTeamResult(
        teamName = teamResult.teamName.content,
        bib = teamResult.bibNumber?.content,
        result = Result(
            time = teamResult.time?.content?.let { convertTimeSec(it) },
            timeBehind = teamResult.timeDiff?.content?.let { convertTimeSec(it) },
            position = teamResult.resultPosition?.content?.takeIf { it != "0" }?.toInt(),
            status = ResultStatus.valueOf(teamResult.teamStatus.value),
        ),
        leg = teamResult.teamMemberResult[0].leg.toInt(),
        legResult = Result(
            time = teamResult.teamMemberResult[0].time?.content?.let { convertTimeSec(it) },
            timeBehind = null,
            position = null,
            status = ResultStatus.valueOf(teamResult.teamStatus.value)
        ),
        eventClass = EventClassConverter.convertEventClass(Event(eventorId = eventor.id, eventorRef = eventId), classResult.eventClass)
    )

    private fun convertTimeSec(time: String?): Int {
        if (time == null) return 0
        return try {
            val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
            val reference = dateFormat.parse("00:00:00")
            val date = dateFormat.parse(time)
            ((date.time - reference.time) / 1000L).toInt()
        } catch (_: ParseException) {
            val dateFormat: DateFormat = SimpleDateFormat("mm:ss")
            val reference = dateFormat.parse("00:00:00")
            val date = dateFormat.parse(time)
            ((date.time - reference.time) / 1000L).toInt()
        }
    }
}
