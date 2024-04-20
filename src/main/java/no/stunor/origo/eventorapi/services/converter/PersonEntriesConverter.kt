package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.calendar.UserCompetitor
import no.stunor.origo.eventorapi.model.calendar.UserRace
import no.stunor.origo.eventorapi.model.origo.user.*
import no.stunor.origo.eventorapi.model.person.Person
import org.iof.eventor.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.ParseException

@Component
class PersonEntriesConverter {
    @Autowired
    private lateinit var eventConverter: EventConverter

    @Autowired
    private lateinit var startListConverter: StartListConverter

    @Autowired
    private lateinit var resultListConverter: ResultListConverter

    @Autowired
    private lateinit var eventClassConverter: EventClassConverter

    @Throws(NumberFormatException::class, ParseException::class)
    fun convertPersonEntries(eventor: Eventor, person: Person, entryList: EntryList, startListList: StartListList, resultListList: ResultListList, eventClassMap: MutableMap<String, EventClassList>): List<UserRace> {
        var result = convertEntryList(eventor, entryList, person, eventClassMap)
        result = convertStartListList(eventor, startListList, person, result)
        result = convertResultList(eventor, resultListList, person, result)
        return result.values.stream().toList()
    }


    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertEntryList(eventor: Eventor, entryList: EntryList, person: Person, eventClassMap: Map<String, EventClassList>): MutableMap<String?, UserRace> {
        val raceMap: MutableMap<String?, UserRace> = HashMap()

        for (entry in entryList.entry) {
            for (eventRaceId in entry.eventRaceId) {
                for (race in entry.event.eventRace) {
                    if (race.eventRaceId.content == eventRaceId.content) {
                        val raceId = eventRaceId.content
                        if (!raceMap.containsKey(raceId)) {
                            raceMap[raceId] = createUserRace(eventor, entry.event, race)
                        }

                        if (!raceMap[raceId]!!.organisationEntries.containsKey(entry.competitor.organisationId.content)) {
                            raceMap[raceId]!!.organisationEntries[entry.competitor.organisationId.content] = 1
                        } else {
                            val count = raceMap[raceId]!!.organisationEntries[entry.competitor.organisationId.content]!!
                            raceMap[raceId]!!.organisationEntries[entry.competitor.organisationId.content] = count + 1
                        }

                        if (entry.competitor.personId.content == person.personId) {
                            raceMap[raceId]!!.userCompetitors.add(createUserCompetitor(person = person, entry = entry, classStart = null, start = null, classResult = null, result = null, eventClassList = eventClassMap[raceId]))
                        }
                    }
                }
            }
        }
        return raceMap
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertStartListList(eventor: Eventor, startListList: StartListList, person: Person, raceMap: MutableMap<String?, UserRace>): MutableMap<String?, UserRace> {
        for (startList in startListList.startList) {
            if (startList.event.eventRace.size == 1) {
                val race = startList.event.eventRace[0]
                val raceId = race.eventRaceId.content

                if (!raceMap.containsKey(raceId)) {
                    raceMap[raceId] = createUserRace(eventor, startList.event, race)
                }

                for (classStart in startList.classStart) {
                    for (start in classStart.personStartOrTeamStart) {
                        if (raceMap[raceId]!!.userCompetitors.isEmpty()) {
                            raceMap[raceId]!!.userCompetitors.add(createUserCompetitor(person, null, classStart, start, null, null, null))
                        } else {
                            val userEntry = raceMap[raceId]!!.userCompetitors[0].personEntry
                            raceMap[raceId]!!.userCompetitors.removeAt(0)
                            raceMap[raceId]!!.userCompetitors.add(updateUserStart(person, userEntry, classStart, start))
                        }
                    }
                }
            } else {
                for (classStart in startList.classStart) {
                    for (start in classStart.personStartOrTeamStart) {
                        if (start is PersonStart) {
                            val raceId: String = start.raceStart[0].eventRaceId.content
                            for (race in startList.event.eventRace) {
                                if (race.eventRaceId.content == raceId) {
                                    if (!raceMap.containsKey(raceId)) {
                                        raceMap[raceId] = createUserRace(eventor, startList.event, race)
                                    }
                                    if (raceMap[raceId]!!.userCompetitors.isEmpty()) {
                                        raceMap[raceId]!!.userCompetitors.add(createUserCompetitor(person, null, classStart, start, null, null, null))
                                    } else {
                                        val userEntry = raceMap[raceId]!!.userCompetitors[0].personEntry
                                        raceMap[raceId]!!.userCompetitors.removeAt(0)
                                        raceMap[raceId]!!.userCompetitors.add(updateUserStart(person, userEntry, classStart, start))
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

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertResultList(eventor: Eventor, resultListList: ResultListList, person: Person, raceMap: MutableMap<String?, UserRace>): MutableMap<String?, UserRace> {
        for (resultList in resultListList.resultList) {
            if (resultList.event.eventRace.size == 1) {
                val race = resultList.event.eventRace[0]
                val raceId = race.eventRaceId.content

                if (!raceMap.containsKey(raceId)) {
                    raceMap[raceId] = createUserRace(eventor, resultList.event, race)
                }

                for (classResult in resultList.classResult) {
                    for (result in classResult.personResultOrTeamResult) {
                        if (raceMap[raceId]!!.userCompetitors.isEmpty()) {
                            raceMap[raceId]!!.userCompetitors.add(createUserCompetitor(person, null, null, null, classResult, result, null))
                        } else {
                            val userEntry = raceMap[raceId]!!.userCompetitors[0].personEntry
                            val personStart = raceMap[raceId]!!.userCompetitors[0].personStart
                            val teamStart = raceMap[raceId]!!.userCompetitors[0].teamStart

                            raceMap[raceId]!!.userCompetitors.removeAt(0)
                            raceMap[raceId]!!.userCompetitors.add(updateUserResult(person, userEntry, personStart, teamStart, classResult, result))
                        }
                    }
                }
            } else {
                for (classResult in resultList.classResult) {
                    for (result in classResult.personResultOrTeamResult) {
                        if (result is PersonResult) {
                            val raceId: String = result.raceResult[0].eventRaceId.content
                            for (race in resultList.event.eventRace) {
                                if (race.eventRaceId.content == raceId) {
                                    if (!raceMap.containsKey(raceId)) {
                                        raceMap[raceId] = createUserRace(eventor, resultList.event, race)
                                    }
                                    if (raceMap[raceId]!!.userCompetitors.isEmpty()) {
                                        raceMap[raceId]!!.userCompetitors.add(createUserCompetitor(person, null, null, null, classResult, result, null))
                                    } else {
                                        val userEntry = raceMap[raceId]!!.userCompetitors[0].personEntry
                                        val personStart = raceMap[raceId]!!.userCompetitors[0].personStart
                                        val teamStart = raceMap[raceId]!!.userCompetitors[0].teamStart
                                        raceMap[raceId]!!.userCompetitors.removeAt(0)
                                        raceMap[raceId]!!.userCompetitors.add(updateUserResult(person, userEntry, personStart, teamStart, classResult, result))
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

    private fun createUserRace(eventor: Eventor, event: Event, race: EventRace): UserRace {
        return UserRace(
                eventor = eventor,
                eventId = event.eventId.content,
                eventName = event.name.content,
                raceId = race.eventRaceId.content,
                raceName = race.name.content,
                canceled = event.eventStatusId.content == "10",
                raceDate = eventConverter.convertRaceDateWithoutTime(race.raceDate),
                userCompetitors = mutableListOf(),
                organisationEntries = mutableMapOf(),
                entryBreaks = eventConverter.convertEntryBreaks(event.entryBreak))
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun createUserCompetitor(person: Person, entry: Entry?, classStart: ClassStart?, start: Any?, classResult: ClassResult?, result: Any?, eventClassList: EventClassList?): UserCompetitor {
        return UserCompetitor(
                person.personId,
                person.name,
                if (entry != null) createUserEntry(entry, eventClassList) else null,
                if (start != null && start is PersonStart) createPersonStart(start, classStart) else null,
                if (start != null && start is TeamStart) createTeamStart(start, classStart) else null,
                if (result != null && result is PersonResult) createPersonResult(result, classResult) else null,
                if (result != null && result is TeamResult) createTeamResult(result, classResult) else null
        )
    }

    private fun updateUserStart(person: Person, userEntry: UserEntry?, classStart: ClassStart, start: Any?): UserCompetitor {
        return UserCompetitor(
                person.personId,
                person.name,
                userEntry,
                if (start != null && start is PersonStart) createPersonStart(start, classStart) else null,
                if (start != null && start is TeamStart) createTeamStart(start, classStart) else null,
                null,
                null)
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun updateUserResult(person: Person, userEntry: UserEntry?, personStart: UserPersonStart?, teamStart: UserTeamStart?, classResult: ClassResult, result: Any?): UserCompetitor {
        return UserCompetitor(
                person.personId,
                person.name,
                userEntry,
                personStart,
                teamStart,
                if (result != null && result is PersonResult) createPersonResult(result, classResult) else null,
                if (result != null && result is TeamResult) createTeamResult(result, classResult) else null
        )
    }

    private fun createUserEntry(entry: Entry, eventClassList: EventClassList?): UserEntry {
        return UserEntry(
                if (entry.entryClass != null && entry.entryClass.isNotEmpty()) eventClassConverter.getEventClassFromId(eventClassList!!, entry.entryClass[0].eventClassId.content) else null,
                if (entry.competitor.cCard != null && entry.competitor.cCard.isNotEmpty()) eventConverter.convertCCard(entry.competitor.cCard[0]) else null)
    }

    private fun createPersonStart(personStart: PersonStart, classStart: ClassStart?): UserPersonStart {
        val start: Start = if (personStart.start != null) {
            personStart.start
        } else {
            personStart.raceStart[0].start
        }

        return UserPersonStart(
                    if (start.startTime != null) startListConverter.convertStartTime(start.startTime) else null,
                    if (start.bibNumber != null) start.bibNumber.content else "",
                    eventClassConverter.convertEventClass(classStart!!.eventClass)
            )
    }

    private fun createTeamStart(teamStart: TeamStart, classStart: ClassStart?): UserTeamStart {
        return UserTeamStart(
                teamStart.teamName.content,
                if (teamStart.startTime != null) startListConverter.convertStartTime(teamStart.startTime) else null,
                if (teamStart.bibNumber != null) teamStart.bibNumber.content else "",
                teamStart.teamMemberStart[0].leg.toInt(),
                eventClassConverter.convertEventClass(classStart!!.eventClass)
        )
    }

    @Throws(NumberFormatException::class, ParseException::class)
    fun createPersonResult(personResult: PersonResult, classResult: ClassResult?): UserPersonResult {
        val result: Result = if (personResult.result != null) {
            personResult.result
        } else {
            personResult.raceResult[0].result
        }

        return UserPersonResult(
                if (result.bibNumber != null) result.bibNumber.content else "",
                resultListConverter.convertPersonResult(result),
                eventClassConverter.convertEventClass(classResult!!.eventClass)
        )
    }

    @Throws(NumberFormatException::class, ParseException::class)
    fun createTeamResult(teamResult: TeamResult, classResult: ClassResult?): UserTeamResult {
        return UserTeamResult(
                teamResult.teamName.content,
                if (teamResult.bibNumber != null) teamResult.bibNumber.content else "",
                resultListConverter.convertTeamResult(teamResult),
                teamResult.teamMemberResult[0].leg.toInt(),
                resultListConverter.convertTimeSec(teamResult.teamMemberResult[0].time.content),
                eventClassConverter.convertEventClass(classResult!!.eventClass)
        )
    }
}