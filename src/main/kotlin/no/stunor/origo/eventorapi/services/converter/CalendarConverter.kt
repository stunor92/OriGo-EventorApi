package no.stunor.origo.eventorapi.services.converter
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.calendar.*
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.competitor.Result
import no.stunor.origo.eventorapi.model.event.competitor.ResultStatus
import no.stunor.origo.eventorapi.model.person.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component


@Component
class CalendarConverter {
    @Autowired
    private lateinit var timeStampConverter: TimeStampConverter

    @Autowired
    private lateinit var entryConverter: EntryConverter

    @Autowired
    private lateinit var eventConverter: EventConverter

    @Autowired
    private lateinit var eventClassConverter: EventClassConverter

    @Autowired
    private lateinit var competitorConverter: CompetitorConverter

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
            eventorId = eventor.eventorId,
            eventId = event.eventId.content,
            eventName = event.name.content,
            raceId = eventRace.eventRaceId.content,
            raceName = eventRace.name.content,
            raceDate = timeStampConverter.parseTimestamp("${eventRace.raceDate.date.content} 00:00:00"),
            type = eventConverter.convertEventForm(event.eventForm),
            classification = eventConverter.convertEventClassification(event.eventClassificationId.content),
            lightCondition = eventConverter.convertLightCondition(eventRace.raceLightCondition),
            distance = eventConverter.convertRaceDistance(eventRace.raceDistance),
            position = if (eventRace.eventCenterPosition != null) eventConverter.convertPosition(eventRace.eventCenterPosition) else null,
            status = eventConverter.convertEventStatus(event.eventStatusId.content),
            disciplines = eventConverter.convertEventDisciplines(event.disciplineIdOrDiscipline),
            organisers = if (event.organiser != null) eventConverter.convertOrganisers(event.organiser.organisationIdOrOrganisation) else listOf(),
            entryBreaks = entryConverter.convertEntryBreaks(event.entryBreak, eventor),
            entries = getEntries(event.eventId.content, eventRace.eventRaceId.content, competitorCountList),
            userEntries = mutableListOf(),
            organisationEntries = getOrganisationEntries(
                event.eventId.content,
                eventRace.eventRaceId.content,
                competitorCountList
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
                    && (competitorCount.eventRaceId == null || competitorCount.eventRaceId == eventRaceId)) {
                    return competitorCount.numberOfEntries.toInt()
                }
            }
        }
        return 0
    }

    private fun getOrganisationEntries(
        eventId: String,
        eventRaceId: String,
        competitorCountList: org.iof.eventor.CompetitorCountList
    ): MutableMap<String, Int> {
        val result: MutableMap<String, Int> = HashMap()
        for (competitorCount in competitorCountList.competitorCount) {
            if (competitorCount.eventId == eventId
                && (competitorCount.eventRaceId == null
                        || competitorCount.eventRaceId == eventRaceId)
                    && competitorCount.organisationCompetitorCount != null) {
                for (organisationCompetitorCount in competitorCount.organisationCompetitorCount) {
                    result[organisationCompetitorCount.organisationId] =
                        organisationCompetitorCount.numberOfEntries.toInt()
                }


            }
        }
        return result
    }

    private fun isSignedUp(eventId: String, competitorCountList: org.iof.eventor.CompetitorCountList?): Boolean {
        if (competitorCountList != null) {
            for (competitorCount in competitorCountList.competitorCount) {
                if (competitorCount.eventId == eventId
                    && competitorCount.classCompetitorCount != null
                    && competitorCount.classCompetitorCount.isNotEmpty()) {
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

        if (entryList == null)
            return raceMap

        for (entry in entryList.entry) {
            for (eventRaceId in entry.eventRaceId) {
                for (race in entry.event.eventRace) {
                    if (race.eventRaceId.content == eventRaceId.content) {
                        val raceId = eventRaceId.content
                        if (!raceMap.containsKey(raceId)) {
                            raceMap[raceId] = generateRace(
                                event = entry.event,
                                eventRace = race,
                                eventor = eventor,
                                competitorCountList = org.iof.eventor.CompetitorCountList()
                            )
                        }

                        if (!raceMap.getValue(raceId).organisationEntries.containsKey(entry.competitor.organisationId.content)) {
                            raceMap.getValue(raceId).organisationEntries[entry.competitor.organisationId.content] = 1
                        } else {
                            val count = raceMap
                                .getValue(raceId)
                                .organisationEntries
                                .getValue(entry.competitor.organisationId.content)
                            raceMap.getValue(raceId).organisationEntries[entry.competitor.organisationId.content] = count + 1
                        }

                        if (entry.competitor.personId.content == person.personId) {
                            raceMap.getValue(raceId).userEntries.add(
                                generateCompetitor(
                                    eventor = eventor,
                                    eventId = entry.event.eventId.content,
                                    person = person,
                                    entry = entry,
                                    classStart = null,
                                    start = null,
                                    classResult = null,
                                    result = null,
                                    eventClassList = eventClassMap[raceId]
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
        return raceMap
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
                                    person =person,
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
            eventClass = if (entry.entryClass != null && entry.entryClass.isNotEmpty()) eventClassConverter.getEventClassFromId(
                eventClassList =eventClassList!!,
                entryClassId = entry.entryClass[0].eventClassId.content,
                eventor = eventor,
                event = Event(eventId = eventId),
            ) else null,
            punchingUnits = competitorConverter.convertPunchingUnits(entry.competitor.cCard),
        )
    }

    private fun createPersonStart(
        eventor: Eventor,
        eventId: String,
        personStart: org.iof.eventor.PersonStart,
        classStart: org.iof.eventor.ClassStart
    ): CalendarPersonStart {
        val start: org.iof.eventor.Start = if (personStart.start != null) {
            personStart.start
        } else {
            personStart.raceStart[0].start
        }

        return CalendarPersonStart(
            startTime = if (start.startTime != null) timeStampConverter.parseTimestamp(
                "${start.startTime.date.content} ${start.startTime.clock.content}",
                eventor
            ) else null,
            bib = if (start.bibNumber != null) start.bibNumber.content else null,
            eventClass = eventClassConverter.convertEventClass(
                eventor = eventor,
                event = Event(eventId = eventId),
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
            startTime = if (teamStart.startTime != null) timeStampConverter.parseTimestamp(
                "${teamStart.startTime.date.content} ${teamStart.startTime.clock.content}",
                eventor
            ) else null,
            bib = if (teamStart.bibNumber != null) teamStart.bibNumber.content else null,
            leg = teamStart.teamMemberStart[0].leg.toInt(),
            eventClass = eventClassConverter.convertEventClass(
                eventor = eventor,
                event = Event(eventId = eventId),
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
                    time = if (result.time != null) competitorConverter.convertTimeSec(result.time.content) else null,
                    timeBehind = if (result.timeDiff != null) competitorConverter.convertTimeSec(result.timeDiff.content) else null,
                    position = if (result.resultPosition != null && result.resultPosition.content != "0") result.resultPosition.content.toInt() else null,
                    status = ResultStatus.valueOf(result.competitorStatus.value),
                ),
                bib = if (result.bibNumber != null) result.bibNumber.content else null,
                eventClass = eventClassConverter.convertEventClass(
                    eventor = eventor,
                    event = Event(eventId = eventId),
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
                time = if (teamResult.time != null) competitorConverter.convertTimeSec(teamResult.time.content) else null,
                timeBehind = if (teamResult.timeDiff != null) competitorConverter.convertTimeSec(teamResult.timeDiff.content) else null,
                position = if (teamResult.resultPosition != null && teamResult.resultPosition.content != "0") teamResult.resultPosition.content.toInt() else null,
                status = ResultStatus.valueOf(teamResult.teamStatus.value),
            ),
            leg = teamResult.teamMemberResult[0].leg.toInt(),
            legResult = Result(
                time = if (teamResult.teamMemberResult[0].time != null) competitorConverter.convertTimeSec(teamResult.teamMemberResult[0].time.content) else null,
                timeBehind = null,
                position = null,
                status = ResultStatus.valueOf(teamResult.teamStatus.value)
            ),
            eventClass = eventClassConverter.convertEventClass(
                eventor = eventor,
                event = Event(eventId = eventId),
                eventClass = classResult.eventClass
            )
        )
    }
}