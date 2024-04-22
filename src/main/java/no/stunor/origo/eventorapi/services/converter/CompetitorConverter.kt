package no.stunor.origo.eventorapi.services.converter

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.calendar.CalendarRace
import no.stunor.origo.eventorapi.model.event.CCard
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.model.event.competitor.PersonCompetitor
import no.stunor.origo.eventorapi.model.person.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Component
class CompetitorConverter {
    @Autowired
    private lateinit var personConverter: PersonConverter

    @Autowired
    private lateinit var organisationRepository: OrganisationRepository


    fun generateCompetitors(eventor: Eventor, resultListList: org.iof.eventor.ResultListList, person: Person): List<Competitor> {
        val competitors: MutableList<Competitor> = mutableListOf()
        for (resultList in resultListList.resultList) {
            if (resultList.event.eventRace.size == 1) {
                val race = resultList.event.eventRace[0]
                for (classResult in resultList.classResult) {
                    for (result in classResult.personResultOrTeamResult) {
                        if (result is org.iof.eventor.PersonResult && result.person.personId.content == person.personId) {
                            competitors.add(
                                    PersonCompetitor(
                                            eventorId =  eventor.eventorId,
                                            eventId = resultList.event.eventId.content,
                                            raceId = race.eventRaceId.content,
                                            eventClassId = classResult.eventClass.eventClassId.content,
                                            personId = person.personId,
                                            name = personConverter.convertPersonName(result.person.personName),
                                            organisation = if(result.organisation != null) organisationRepository.findByOrganisationIdAndEventorId(result.organisation.organisationId.content, eventor.eventorId).block() else null,
                                            birthYear = personConverter.convertBirthYear(result.person.birthDate),
                                            nationality = result.person.nationality.country.alpha3.value,
                                            gender = personConverter.convertGender(result.person.sex),
                                            cCard = null,
                                            bib = null,
                                            startTime =  if (result.result.startTime != null) convertStartTime(result.result.startTime) else null,
                                            finishTime = if (result.result.finishTime != null) convertFinishTime(result.result.finishTime) else null,
                                            status = result.result.competitorStatus.value,
                                            position =  if (result.result.resultPosition != null && result.result.resultPosition.content != "0") result.result.resultPosition.content.toInt() else null,
                                            time = if (result.result.time != null) convertTimeSec(result.result.time.content) else null,
                                            timeBehind = if (result.result.timeDiff != null) convertTimeSec(result.result.timeDiff.content) else null,
                                            splitTimes = listOf(),
                                            entryFeeIds = listOf()
                                    )
                            )
                        } else if (result is org.iof.eventor.TeamResult) {
                            //TODO
                        }

                    }
                }
            } else {
                for (classResult in resultList.classResult) {
                    for (result in classResult.personResultOrTeamResult) {
                        if (result is org.iof.eventor.PersonResult && result.person.personId.content == person.personId) {
                            for (raceResult in result.raceResult){
                                competitors.add(
                                        PersonCompetitor(
                                                eventorId =  eventor.eventorId,
                                                eventId = resultList.event.eventId.content,
                                                raceId = raceResult.eventRaceId.content,
                                                eventClassId = classResult.eventClass.eventClassId.content,
                                                personId = person.personId,
                                                name = personConverter.convertPersonName(result.person.personName),
                                                organisation = if(result.organisation != null) organisationRepository.findByOrganisationIdAndEventorId(result.organisation.organisationId.content, eventor.eventorId).block() else null,
                                                birthYear = personConverter.convertBirthYear(result.person.birthDate),
                                                nationality = result.person.nationality.country.alpha3.value,
                                                gender = personConverter.convertGender(result.person.sex),
                                                cCard = null,
                                                bib = null,
                                                startTime =  if (result.result.startTime != null) convertStartTime(result.result.startTime) else null,
                                                finishTime = if (result.result.finishTime != null) convertFinishTime(result.result.finishTime) else null,
                                                status = result.result.competitorStatus.value,
                                                position =  if (result.result.resultPosition != null && result.result.resultPosition.content != "0") result.result.resultPosition.content.toInt() else null,
                                                time = if (result.result.time != null) convertTimeSec(result.result.time.content) else null,
                                                timeBehind = if (result.result.timeDiff != null) convertTimeSec(result.result.timeDiff.content) else null,
                                                splitTimes = listOf(),
                                                entryFeeIds = listOf()
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        return competitors
    }

    fun generateCompetitors(eventor: Eventor, startListList: org.iof.eventor.StartListList, person: Person): List<Competitor> {
        val competitors: MutableList<Competitor> = mutableListOf()
        for (startList in startListList.startList) {
            if (startList.event.eventRace.size == 1) {
                val race = startList.event.eventRace[0]
                for (classStart in startList.classStart) {
                    for (start in classStart.personStartOrTeamStart) {
                        if (start is org.iof.eventor.PersonStart && start.person.personId.content == person.personId) {
                            competitors.add(
                                    PersonCompetitor(
                                            eventorId =  eventor.eventorId,
                                            eventId = startList.event.eventId.content,
                                            raceId = race.eventRaceId.content,
                                            eventClassId = classStart.eventClass.eventClassId.content,
                                            personId = person.personId,
                                            name = personConverter.convertPersonName(start.person.personName),
                                            organisation = if(start.organisation != null) organisationRepository.findByOrganisationIdAndEventorId(start.organisation.organisationId.content, eventor.eventorId).block() else null,
                                            birthYear = personConverter.convertBirthYear(start.person.birthDate),
                                            nationality = start.person.nationality.country.alpha3.value,
                                            gender = personConverter.convertGender(start.person.sex),
                                            cCard = null,
                                            bib = null,
                                            startTime =  if (start.start.startTime != null) convertStartTime(start.start.startTime) else null,
                                            finishTime = null,
                                            status = "NOT_STARTED",
                                            position =  null,
                                            time = null,
                                            timeBehind = null,
                                            splitTimes = listOf(),
                                            entryFeeIds = listOf()
                                    )
                            )
                        } else if (start is org.iof.eventor.TeamStart) {
                            //TODO
                        }

                    }
                }
            } else {
                for (classStart in startList.classStart) {
                    for (start in classStart.personStartOrTeamStart) {
                        if (start is org.iof.eventor.PersonStart && start.person.personId.content == person.personId) {
                            for (raceStart in start.raceStart){
                                competitors.add(
                                        PersonCompetitor(
                                                eventorId =  eventor.eventorId,
                                                eventId = startList.event.eventId.content,
                                                raceId = raceStart.eventRaceId.content,
                                                eventClassId = classStart.eventClass.eventClassId.content,
                                                personId = person.personId,
                                                name = personConverter.convertPersonName(start.person.personName),
                                                organisation = if(start.organisation != null) organisationRepository.findByOrganisationIdAndEventorId(start.organisation.organisationId.content, eventor.eventorId).block() else null,
                                                birthYear = personConverter.convertBirthYear(start.person.birthDate),
                                                nationality = start.person.nationality.country.alpha3.value,
                                                gender = personConverter.convertGender(start.person.sex),
                                                cCard = null,
                                                bib = null,
                                                startTime =  if (start.start.startTime != null) convertStartTime(start.start.startTime) else null,
                                                finishTime = null,
                                                status = "NOT_STARTED",
                                                position =  null,
                                                time = null,
                                                timeBehind = null,
                                                splitTimes = listOf(),
                                                entryFeeIds = listOf()
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }

        return competitors
    }

    fun generateCompetitors(eventor: Eventor, entryList: org.iof.eventor.EntryList, person: Person): Collection<Competitor> {
        val competitors: MutableList<Competitor> = mutableListOf()

        for (entry in entryList.entry) {
            if (entry.competitor?.person?.personId?.content == person.personId) {
                for (eventRaceId in entry.eventRaceId) {
                    competitors.add(
                            PersonCompetitor(
                                    eventorId =  eventor.eventorId,
                                    eventId = entry.eventId.content,
                                    raceId = eventRaceId.content,
                                    eventClassId = entry.entryClass[0].eventClassId.content,
                                    personId = person.personId,
                                    name = personConverter.convertPersonName(entry.competitor.person.personName),
                                    organisation = if(!entry.organisationId.isNullOrEmpty()) organisationRepository.findByOrganisationIdAndEventorId(entry.organisationId[0].content, eventor.eventorId).block() else null,
                                    birthYear = personConverter.convertBirthYear(entry.competitor.person.birthDate),
                                    nationality = entry.competitor.person.nationality.country.alpha3.value,
                                    gender = personConverter.convertGender(entry.competitor.person.sex),
                                    cCard = if(!entry.competitor.cCard.isNullOrEmpty()) convertCCard(entry.competitor.cCard[0]) else null,
                                    bib = entry.bibNumber?.content,
                                    startTime =  null,
                                    finishTime = null,
                                    status = "ENTRY",
                                    position =  null,
                                    time = null,
                                    timeBehind = null,
                                    splitTimes = listOf(),
                                    entryFeeIds = listOf()
                            )
                    )


                }
                //TODO entry.teamCompetitor?..personId?.content == person.personId
            } else if (false) {
            }
        }
        return competitors
    }

    fun convertTimeSec(time: String?): Int? {
        val date: Date
        val reference: Date
        try {
            val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
            reference = dateFormat.parse("00:00:00")
            date = dateFormat.parse(time)
            val seconds = (date.time - reference.time) / 1000L
            return seconds.toInt()
        } catch (e: ParseException) {
            return null
        }

    }

    fun convertStartTime(time: org.iof.eventor.StartTime): Timestamp {
        val timeString = time.date.content + "T" + time.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }


    private fun convertFinishTime(time: org.iof.eventor.FinishTime): Timestamp {
        val timeString = time.date.content + "T" + time.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }

    fun convertCCard(cCard: org.iof.eventor.CCard): CCard {
        return CCard(cCard.cCardId.content, cCard.punchingUnitType.value)
    }
}