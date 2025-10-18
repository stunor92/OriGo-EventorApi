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
import java.util.Date


@Component
class CalendarConverter(
    var organisationRepository: OrganisationRepository,
    var regionRepository: RegionRepository
) {
    var timeStampConverter = TimeStampConverter()
    var feeConverter = FeeConverter()
    var eventConverter = EventConverter()

    var organisationConverter = OrganisationConverter(
        organisationRepository = organisationRepository,
        regionRepository = regionRepository
    )
    var entryListConverter = EntryListConverter()

    fun convertEvents(
        eventList: org.iof.eventor.EventList?,
        eventor: Eventor,
        competitorCountList: org.iof.eventor.CompetitorCountList
    ): List<CalendarRace> {
        val result = mutableListOf<CalendarRace>()
        for (event in eventList!!.event) {
            result.addAll(convertEvent(event, eventor, competitorCountList))
        }
        return result
    }

    private fun convertEvent(
        event: org.iof.eventor.Event,
        eventor: Eventor,
        competitorCountList: org.iof.eventor.CompetitorCountList
    ): List<CalendarRace> {
        val result = mutableListOf<CalendarRace>()
        for (eventRace in event.eventRace) {
            result.add(generateRace(event, eventRace, eventor, competitorCountList))
        }
        return result
    }

    private fun generateRace(
        event: org.iof.eventor.Event,
        eventRace: org.iof.eventor.EventRace,
        eventor: Eventor,
        competitorCountList: org.iof.eventor.CompetitorCountList
    ): CalendarRace {
        return CalendarRace(
            eventor = eventor,
            eventId = event.eventId.content,
            eventName = event.name.content,
            raceId = eventRace.eventRaceId.content,
            raceName = eventRace.name.content,
            raceDate = timeStampConverter.parseDate("${eventRace.raceDate.date.content} 00:00:00"),
            type = eventConverter.convertEventForm(event.eventForm),
            classification = eventConverter.convertEventClassification(event.eventClassificationId.content),
            lightCondition = eventConverter.convertLightCondition(eventRace.raceLightCondition),
            distance = eventConverter.convertRaceDistance(eventRace.raceDistance),
            position = if (eventRace.eventCenterPosition != null) eventConverter.convertPosition(eventRace.eventCenterPosition) else null,
            status = eventConverter.convertEventStatus(event.eventStatusId.content),
            disciplines = eventConverter.convertEventDisciplines(event.disciplineIdOrDiscipline),
            organisers = if (event.organiser != null) organisationConverter.convertOrganisations(
                event.organiser.organisationIdOrOrganisation,
                eventor
            ) else listOf(),
            entryBreaks = feeConverter.convertEntryBreaks(event.entryBreak, eventor),
            entries = getEntries(event.eventId.content, eventRace.eventRaceId.content, competitorCountList),
            userEntries = mutableListOf(),
            organisationEntries = getOrganisationEntries(
                event.eventId.content,
                eventRace.eventRaceId.content,
                competitorCountList,
                eventor
            ),
            signedUp = isSignedUp(event.eventId.content, competitorCountList),
            startList = eventConverter.hasStartList(event.hashTableEntry, eventRace.eventRaceId.content),
            resultList = eventConverter.hasResultList(event.hashTableEntry, eventRace.eventRaceId.content),
            livelox = eventConverter.hasLivelox(event.hashTableEntry)
        )
    }

    private fun getEntries(
        eventId: String,
        eventRaceId: String,
        competitorCountList: org.iof.eventor.CompetitorCountList?
    ): Int {
        if (competitorCountList != null) {
            for (competitorCount in competitorCountList.competitorCount) {
                if (competitorCount.eventId == eventId
                    && (competitorCount.eventRaceId == null || competitorCount.eventRaceId == eventRaceId)
                ) {
                    return competitorCount.numberOfEntries.toInt()
                }
            }
        }
        return 0
    }

    private fun getOrganisationEntries(
        eventId: String,
        eventRaceId: String,
        competitorCountList: org.iof.eventor.CompetitorCountList,
        eventor: Eventor
    ): MutableList<OrganisationEntries> {
        val result = mutableListOf<OrganisationEntries>()
        for (competitorCount in competitorCountList.competitorCount) {
            if (!isRelevantCompetitorCount(competitorCount, eventId, eventRaceId)) continue
            val orgCounts: List<org.iof.eventor.OrganisationCompetitorCount> = competitorCount.organisationCompetitorCount ?: continue
            for (organisationCompetitorCount in orgCounts) {
                val organisation =
                    organisationConverter.convertOrganisation(organisationCompetitorCount.organisationId, eventor)
                        ?: continue
                result.add(OrganisationEntries(organisation, organisationCompetitorCount.numberOfEntries.toInt()))
            }
        }
        return result
    }

    private fun isRelevantCompetitorCount(
        competitorCount: org.iof.eventor.CompetitorCount,
        eventId: String,
        eventRaceId: String
    ): Boolean {
        return competitorCount.eventId == eventId &&
                (competitorCount.eventRaceId == null || competitorCount.eventRaceId == eventRaceId) &&
                competitorCount.organisationCompetitorCount != null
    }

    private fun isSignedUp(eventId: String, competitorCountList: org.iof.eventor.CompetitorCountList?): Boolean {
        if (competitorCountList != null) {
            for (competitorCount in competitorCountList.competitorCount) {
                if (competitorCount.eventId == eventId
                    && competitorCount.classCompetitorCount != null
                    && competitorCount.classCompetitorCount.isNotEmpty()
                ) {
                    return true
                }
            }
        }
        return false
    }

    fun convertEntryList(
        eventor: Eventor,
        entryList: org.iof.eventor.EntryList?,
        person: Person,
        eventClassMap: Map<String, org.iof.eventor.EventClassList>
    ): MutableMap<String?, CalendarRace> {
        val raceMap: MutableMap<String?, CalendarRace> = HashMap()
        if (entryList == null) return raceMap

        for (entry in entryList.entry) {
            for (eventRaceId in entry.eventRaceId) {
                val raceId = eventRaceId.content
                val race = entry.event.eventRace.find { it.eventRaceId.content == raceId } ?: continue
                val organisation =
                    organisationConverter.convertOrganisation(entry.competitor.organisationId.content, eventor)

                if (!raceMap.containsKey(raceId)) {
                    raceMap[raceId] = generateRace(
                        event = entry.event,
                        eventRace = race,
                        eventor = eventor,
                        competitorCountList = org.iof.eventor.CompetitorCountList()
                    )
                }

                organisation?.let { raceMap.getValue(raceId).organisationEntries = updateOrganisationEntries(raceMap.getValue(raceId).organisationEntries, it) }
                if (entry.competitor.personId.content == person.personId) {
                    updateUserEntries(
                        raceMap,
                        raceId,
                        eventor,
                        entry.event.eventId.content,
                        person,
                        entry,
                        eventClassMap[raceId]
                    )
                }
                if (raceMap.getValue(raceId).userEntries.isNotEmpty()) {
                    raceMap.getValue(raceId).signedUp = true
                }
            }
        }
        return raceMap
    }

    private fun updateOrganisationEntries(
        organisationEntries: MutableList<OrganisationEntries>,
        organisation: Organisation
    ): MutableList<OrganisationEntries> {
        if(organisationEntries.none { it.organisation == organisation }) {
            organisationEntries.add(OrganisationEntries(organisation, 0))
        }
        val count = organisationEntries.first { it.organisation == organisation }.entries + 1
        organisationEntries.first { it.organisation == organisation }.entries = count
        return organisationEntries
    }

    private fun updateUserEntries(
        raceMap: Map<String?, CalendarRace>,
        raceId: String?,
        eventor: Eventor,
        eventId: String,
        person: Person,
        entry: org.iof.eventor.Entry,
        eventClassList: org.iof.eventor.EventClassList?
    ) {
        raceMap.getValue(raceId).userEntries.add(
            generateCompetitor(
                eventor = eventor,
                eventId = eventId,
                person = person,
                entry = entry,
                classStart = null,
                start = null,
                classResult = null,
                result = null,
                eventClassList = eventClassList
            )
        )
    }

    fun convertStartListList(
        eventor: Eventor,
        startListList: org.iof.eventor.StartListList?,
        person: Person,
        raceMap: MutableMap<String?, CalendarRace>
    ): MutableMap<String?, CalendarRace> {
        if (startListList == null)
            return raceMap

        for (startList in startListList.startList) {
            if (startList.event.eventRace.size == 1) {
                val race = startList.event.eventRace[0]
                val raceId = race.eventRaceId.content

                if (!raceMap.containsKey(raceId)) {
                    raceMap[raceId] = generateRace(
                        eventor = eventor,
                        event = startList.event,
                        eventRace = race,
                        competitorCountList = org.iof.eventor.CompetitorCountList()
                    )
                }

                for (classStart in startList.classStart) {
                    for (start in classStart.personStartOrTeamStart) {
                        if (raceMap.getValue(raceId).userEntries.isEmpty()) {
                            raceMap.getValue(raceId).userEntries.add(
                                generateCompetitor(
                                    eventor = eventor,
                                    eventId = startList.event.eventId.content,
                                    person = person,
                                    entry = null,
                                    classStart = classStart,
                                    start = start,
                                    classResult = null,
                                    result = null,
                                    eventClassList = null
                                )
                            )
                        } else {
                            val userEntry = raceMap.getValue(raceId).userEntries[0].personEntry
                            raceMap.getValue(raceId).userEntries.removeAt(0)
                            raceMap.getValue(raceId).userEntries.add(
                                updateUserStart(
                                    eventor = eventor,
                                    eventId = startList.event.eventId.content,
                                    person = person,
                                    userEntry = userEntry,
                                    classStart = classStart,
                                    start = start
                                )
                            )
                        }
                    }
                }
                if (raceMap.getValue(raceId).userEntries.isNotEmpty()) {
                    raceMap.getValue(raceId).signedUp = true
                }
            } else {
                for (classStart in startList.classStart) {
                    for (start in classStart.personStartOrTeamStart) {
                        if (start is org.iof.eventor.PersonStart) {
                            val raceId: String = start.raceStart[0].eventRaceId.content
                            for (race in startList.event.eventRace) {
                                if (race.eventRaceId.content == raceId) {
                                    if (!raceMap.containsKey(raceId)) {
                                        raceMap[raceId] = generateRace(
                                            eventor = eventor,
                                            event = startList.event,
                                            eventRace = race,
                                            competitorCountList = org.iof.eventor.CompetitorCountList()
                                        )
                                    }
                                    if (raceMap.getValue(raceId).userEntries.isEmpty()) {
                                        raceMap.getValue(raceId).userEntries.add(
                                            generateCompetitor(
                                                eventor = eventor,
                                                eventId = startList.event.eventId.content,
                                                person = person,
                                                entry = null,
                                                classStart = classStart,
                                                start = start,
                                                classResult = null,
                                                result = null,
                                                eventClassList = null
                                            )
                                        )
                                    } else {
                                        val userEntry = raceMap.getValue(raceId).userEntries[0].personEntry
                                        raceMap.getValue(raceId).userEntries.removeAt(0)
                                        raceMap.getValue(raceId).userEntries.add(
                                            updateUserStart(
                                                eventor = eventor,
                                                eventId = startList.event.eventId.content,
                                                person = person,
                                                userEntry = userEntry,
                                                classStart = classStart,
                                                start = start
                                            )
                                        )
                                    }
                                    if (raceMap.getValue(raceId).userEntries.isNotEmpty()) {
                                        raceMap.getValue(raceId).signedUp = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return raceMap
    }

    fun convertResultList(
        eventor: Eventor,
        resultListList: org.iof.eventor.ResultListList?,
        person: Person,
        raceMap: MutableMap<String?, CalendarRace>
    ): MutableMap<String?, CalendarRace> {
        if (resultListList == null)
            return raceMap
        for (resultList in resultListList.resultList) {
            if (resultList.event.eventRace.size == 1) {
                val race = resultList.event.eventRace[0]
                val raceId = race.eventRaceId.content

                if (!raceMap.containsKey(raceId)) {
                    raceMap[raceId] = generateRace(
                        eventor = eventor,
                        event = resultList.event,
                        eventRace = race,
                        competitorCountList = org.iof.eventor.CompetitorCountList()
                    )
                }

                for (classResult in resultList.classResult) {
                    for (result in classResult.personResultOrTeamResult) {
                        if (raceMap.getValue(raceId).userEntries.isEmpty()) {
                            raceMap.getValue(raceId).userEntries.add(
                                generateCompetitor(
                                    eventor = eventor,
                                    eventId = resultList.event.eventId.content,
                                    person = person,
                                    entry = null,
                                    classStart = null,
                                    start = null,
                                    classResult = classResult,
                                    result = result,
                                    eventClassList = null
                                )
                            )
                        } else {
                            val userEntry = raceMap.getValue(raceId).userEntries[0].personEntry
                            val personStart = raceMap.getValue(raceId).userEntries[0].personStart
                            val teamStart = raceMap.getValue(raceId).userEntries[0].teamStart

                            raceMap.getValue(raceId).userEntries.removeAt(0)
                            raceMap.getValue(raceId).userEntries.add(
                                updateUserResult(
                                    eventor = eventor,
                                    eventId = resultList.event.eventId.content,
                                    person = person,
                                    userEntry = userEntry,
                                    personStart = personStart,
                                    teamStart = teamStart,
                                    classResult = classResult,
                                    result = result
                                )
                            )
                        }
                        if (raceMap.getValue(raceId).userEntries.isNotEmpty()) {
                            raceMap.getValue(raceId).signedUp = true
                        }
                    }
                }
            } else {
                for (classResult in resultList.classResult) {
                    for (result in classResult.personResultOrTeamResult) {
                        if (result is org.iof.eventor.PersonResult) {
                            val raceId: String = result.raceResult[0].eventRaceId.content
                            for (race in resultList.event.eventRace) {
                                if (race.eventRaceId.content == raceId) {
                                    if (!raceMap.containsKey(raceId)) {
                                        raceMap[raceId] = generateRace(
                                            eventor = eventor,
                                            event = resultList.event,
                                            eventRace = race,
                                            competitorCountList = org.iof.eventor.CompetitorCountList()
                                        )
                                    }
                                    if (raceMap.getValue(raceId).userEntries.isEmpty()) {
                                        raceMap.getValue(raceId).userEntries.add(
                                            generateCompetitor(
                                                eventor = eventor,
                                                eventId = resultList.event.eventId.content,
                                                person = person,
                                                entry = null,
                                                classStart = null,
                                                start = null,
                                                classResult = classResult,
                                                result = result,
                                                eventClassList = null
                                            )
                                        )
                                    } else {
                                        val userEntry = raceMap.getValue(raceId).userEntries[0].personEntry
                                        val personStart = raceMap.getValue(raceId).userEntries[0].personStart
                                        val teamStart = raceMap.getValue(raceId).userEntries[0].teamStart
                                        raceMap.getValue(raceId).userEntries.removeAt(0)
                                        raceMap.getValue(raceId).userEntries.add(
                                            updateUserResult(
                                                eventor = eventor,
                                                eventId = resultList.event.eventId.content,
                                                person = person,
                                                userEntry = userEntry,
                                                personStart = personStart,
                                                teamStart = teamStart,
                                                classResult = classResult,
                                                result = result
                                            )
                                        )
                                    }
                                    if (raceMap.getValue(raceId).userEntries.isNotEmpty()) {
                                        raceMap.getValue(raceId).signedUp = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return raceMap
    }

    private fun generateCompetitor(
        eventor: Eventor,
        eventId: String,
        person: Person,
        entry: org.iof.eventor.Entry?,
        classStart: org.iof.eventor.ClassStart?,
        start: Any?,
        classResult: org.iof.eventor.ClassResult?,
        result: Any?,
        eventClassList: org.iof.eventor.EventClassList?
    ): CalendarCompetitor {
        return CalendarCompetitor(
            person.personId,
            person.name,
            if (entry != null) createUserEntry(
                entry = entry,
                eventClassList = eventClassList,
                eventor = eventor,
                eventId = eventId
            ) else null,
            if (start != null && start is org.iof.eventor.PersonStart && classStart != null) createPersonStart(
                eventor = eventor,
                eventId = eventId,
                personStart = start,
                classStart = classStart
            ) else null,
            if (start != null && start is org.iof.eventor.TeamStart && classStart != null) createTeamStart(
                eventor = eventor,
                eventId = eventId,
                teamStart = start,
                classStart = classStart
            ) else null,
            if (result != null && result is org.iof.eventor.PersonResult && classResult != null) createPersonResult(
                eventor = eventor,
                eventId = eventId,
                personResult = result,
                classResult = classResult
            ) else null,
            if (result != null && result is org.iof.eventor.TeamResult && classResult != null) createTeamResult(
                eventor = eventor,
                eventId = eventId,
                teamResult = result,
                classResult = classResult
            ) else null
        )
    }

    private fun updateUserStart(
        eventor: Eventor,
        eventId: String,
        person: Person,
        userEntry: CalendarEntry?,
        classStart: org.iof.eventor.ClassStart,
        start: Any?
    ): CalendarCompetitor {
        return CalendarCompetitor(
            person.personId,
            person.name,
            userEntry,
            if (start != null && start is org.iof.eventor.PersonStart) createPersonStart(
                eventor = eventor,
                eventId = eventId,
                personStart = start,
                classStart = classStart
            ) else null,
            if (start != null && start is org.iof.eventor.TeamStart) createTeamStart(
                eventor = eventor,
                eventId = eventId,
                teamStart = start,
                classStart = classStart
            ) else null,
            null,
            null
        )
    }

    private fun updateUserResult(
        eventor: Eventor,
        eventId: String,
        person: Person,
        userEntry: CalendarEntry?,
        personStart: CalendarPersonStart?,
        teamStart: CalendarTeamStart?,
        classResult: org.iof.eventor.ClassResult,
        result: Any?
    ): CalendarCompetitor {
        return CalendarCompetitor(
            person.personId,
            person.name,
            userEntry,
            personStart,
            teamStart,
            if (result != null && result is org.iof.eventor.PersonResult) createPersonResult(
                eventor = eventor,
                eventId = eventId,
                personResult = result,
                classResult = classResult
            ) else null,
            if (result != null && result is org.iof.eventor.TeamResult) createTeamResult(
                eventor = eventor,
                eventId = eventId,
                teamResult = result,
                classResult = classResult
            ) else null
        )
    }

    private fun createUserEntry(
        entry: org.iof.eventor.Entry,
        eventClassList: org.iof.eventor.EventClassList?,
        eventId: String,
        eventor: Eventor
    ): CalendarEntry {
        return CalendarEntry(
            eventClass = if (entry.entryClass != null && entry.entryClass.isNotEmpty()) EventClassConverter.getEventClassFromId(
                eventClassList = eventClassList!!,
                entryClassId = entry.entryClass[0].eventClassId.content,
                eventor = eventor,
                event = Event(
                    eventor = eventor,
                    eventId = eventId
                ),
            ) else null,
            punchingUnits = entryListConverter.convertPunchingUnits(entry.competitor.cCard),
        )
    }

    private fun createPersonStart(
        eventor: Eventor,
        eventId: String,
        personStart: org.iof.eventor.PersonStart,
        classStart: org.iof.eventor.ClassStart
    ): CalendarPersonStart {
        val start: org.iof.eventor.Start = personStart.start ?: personStart.raceStart[0].start

        return CalendarPersonStart(
            startTime = if (start.startTime != null) timeStampConverter.parseDate(
                "${start.startTime.date.content} ${start.startTime.clock.content}",
                eventor
            ) else null,
            bib = if (start.bibNumber != null) start.bibNumber.content else null,
            eventClass = EventClassConverter.convertEventClass(
                eventor = eventor,
                event = Event(
                    eventor = eventor,
                    eventId = eventId
                ),
                eventClass = classStart.eventClass
            )
        )
    }

    private fun createTeamStart(
        eventor: Eventor,
        eventId: String,
        teamStart: org.iof.eventor.TeamStart,
        classStart: org.iof.eventor.ClassStart
    ): CalendarTeamStart {
        return CalendarTeamStart(
            teamName = teamStart.teamName.content,
            startTime = if (teamStart.startTime != null) timeStampConverter.parseDate(
                "${teamStart.startTime.date.content} ${teamStart.startTime.clock.content}",
                eventor
            ) else null,
            bib = if (teamStart.bibNumber != null) teamStart.bibNumber.content else null,
            leg = teamStart.teamMemberStart[0].leg.toInt(),
            eventClass = EventClassConverter.convertEventClass(
                eventor = eventor,
                event = Event(
                    eventor = eventor,
                    eventId = eventId
                ),
                eventClass = classStart.eventClass
            )
        )
    }

    private fun createPersonResult(
        eventor: Eventor,
        eventId: String,
        personResult: org.iof.eventor.PersonResult,
        classResult: org.iof.eventor.ClassResult
    ): CalendarPersonResult? {
        val result: org.iof.eventor.Result? =
            if (personResult.result != null && personResult.result.competitorStatus.value != "Inactive") {
                personResult.result
            } else if (!personResult.raceResult.isNullOrEmpty() && personResult.raceResult[0].result.competitorStatus.value != "Inactive") {
                personResult.raceResult[0].result
            } else {
                null
            }

        if (result != null) {
            return CalendarPersonResult(
                result = Result(
                    time = if (result.time != null) convertTimeSec(result.time.content) else null,
                    timeBehind = if (result.timeDiff != null) convertTimeSec(result.timeDiff.content) else null,
                    position = if (result.resultPosition != null && result.resultPosition.content != "0") result.resultPosition.content.toInt() else null,
                    status = ResultStatus.valueOf(result.competitorStatus.value),
                ),
                bib = if (result.bibNumber != null) result.bibNumber.content else null,
                eventClass = EventClassConverter.convertEventClass(
                    eventor = eventor,
                    event = Event(
                        eventor = eventor,
                        eventId = eventId
                    ),
                    eventClass = classResult.eventClass
                )
            )
        }
        return null
    }

    private fun createTeamResult(
        eventor: Eventor,
        eventId: String,
        teamResult: org.iof.eventor.TeamResult,
        classResult: org.iof.eventor.ClassResult
    ): CalendarTeamResult {
        return CalendarTeamResult(
            teamName = teamResult.teamName.content,
            bib = if (teamResult.bibNumber != null) teamResult.bibNumber.content else null,
            result = Result(
                time = if (teamResult.time != null) convertTimeSec(teamResult.time.content) else null,
                timeBehind = if (teamResult.timeDiff != null) convertTimeSec(teamResult.timeDiff.content) else null,
                position = if (teamResult.resultPosition != null && teamResult.resultPosition.content != "0") teamResult.resultPosition.content.toInt() else null,
                status = ResultStatus.valueOf(teamResult.teamStatus.value),
            ),
            leg = teamResult.teamMemberResult[0].leg.toInt(),
            legResult = Result(
                time = if (teamResult.teamMemberResult[0].time != null) convertTimeSec(teamResult.teamMemberResult[0].time.content) else null,
                timeBehind = null,
                position = null,
                status = ResultStatus.valueOf(teamResult.teamStatus.value)
            ),
            eventClass = EventClassConverter.convertEventClass(
                eventor = eventor,
                event = Event(
                    eventor = eventor,
                    eventId = eventId
                ),
                eventClass = classResult.eventClass
            )
        )
    }

    private fun convertTimeSec(time: String?): Int {
        var date: Date
        var reference: Date
        try {
            val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
            reference = dateFormat.parse("00:00:00")
            date = dateFormat.parse(time)
            val seconds = (date.time - reference.time) / 1000L
            return seconds.toInt()
        } catch (_: ParseException) {
            val dateFormat: DateFormat = SimpleDateFormat("mm:ss")
            reference = dateFormat.parse("00:00:00")
            date = dateFormat.parse(time)
            val seconds = (date.time - reference.time) / 1000L
            return seconds.toInt()
        }

    }
}