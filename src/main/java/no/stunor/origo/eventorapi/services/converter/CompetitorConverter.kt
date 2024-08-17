package no.stunor.origo.eventorapi.services.converter

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.event.PunchingUnitType
import no.stunor.origo.eventorapi.model.event.competitor.*
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.person.Person
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class CompetitorConverter {
    @Autowired
    private lateinit var personConverter: PersonConverter

    @Autowired
    private lateinit var organisationRepository: OrganisationRepository


    fun generateCompetitors(
        eventor: Eventor,
        resultListList: org.iof.eventor.ResultListList,
        person: Person
    ): List<Competitor> {
        val competitors: MutableList<Competitor> = mutableListOf()
        for (resultList in resultListList.resultList) {
            if (resultList.event.eventRace.size == 1) {
                val race = resultList.event.eventRace[0]
                for (classResult in resultList.classResult) {
                    for (result in classResult.personResultOrTeamResult) {
                        if (result is org.iof.eventor.PersonResult && result.person.personId.content == person.personId) {
                            competitors.add(
                                PersonCompetitor(
                                    raceId = race.eventRaceId.content,
                                    eventClassId = classResult.eventClass.eventClassId.content,
                                    personId = person.personId,
                                    name = personConverter.convertPersonName(result.person.personName),
                                    organisation = if (result.organisation != null) organisationRepository.findByOrganisationIdAndEventorId(
                                        result.organisation.organisationId.content,
                                        eventor.eventorId
                                    ) else null,
                                    birthYear = personConverter.convertBirthYear(result.person.birthDate),
                                    nationality = result.person.nationality.country.alpha3.value,
                                    gender = personConverter.convertGender(result.person.sex),
                                    punchingUnit = null,
                                    bib = null,
                                    status = CompetitorStatus.Finished,
                                    startTime = if (result.result.startTime != null) convertStartTime(
                                        result.result.startTime,
                                        eventor
                                    ) else null,
                                    finishTime = if (result.result.finishTime != null) convertFinishTime(
                                        result.result.finishTime,
                                        eventor
                                    ) else null,
                                    result = Result(
                                        status = ResultStatus.valueOf(result.result.competitorStatus.value),
                                        position = if (result.result.resultPosition != null && result.result.resultPosition.content != "0") result.result.resultPosition.content.toInt() else null,
                                        time = if (result.result.time != null) convertTimeSec(result.result.time.content) else null,
                                        timeBehind = if (result.result.timeDiff != null) convertTimeSec(result.result.timeDiff.content) else null
                                    ),
                                    splitTimes = listOf(),
                                    entryFeeIds = listOf()
                                )
                            )
                        } else if (result is org.iof.eventor.TeamResult) {
                            competitors.add(
                                TeamCompetitor(
                                    raceId = race.eventRaceId.content,
                                    eventClassId = classResult.eventClass.eventClassId.content,
                                    name = result.teamName.content,
                                    organisations = convertOrganisations(
                                        result.organisationIdOrOrganisationOrCountryId,
                                        eventor
                                    ),
                                    bib = null,
                                    status = CompetitorStatus.Finished,
                                    startTime = if (result.startTime != null) convertStartTime(
                                        result.startTime,
                                        eventor
                                    ) else null,
                                    finishTime = if (result.finishTime != null) convertFinishTime(
                                        result.finishTime,
                                        eventor
                                    ) else null,
                                    result = Result(
                                        status = ResultStatus.valueOf(result.teamStatus.value),
                                        position = if (result.resultPosition != null && result.resultPosition.content != "0") result.resultPosition.content.toInt() else null,
                                        time = if (result.time != null) convertTimeSec(result.time.content) else null,
                                        timeBehind = if (result.timeDiff != null) convertTimeSec(result.timeDiff.content) else null
                                    ),
                                    teamMembers = convertTeamMemberResults(eventor, result.teamMemberResult)
                                )
                            )
                        }

                    }
                }
            } else {
                for (classResult in resultList.classResult) {
                    for (result in classResult.personResultOrTeamResult) {
                        if (result is org.iof.eventor.PersonResult && result.person.personId.content == person.personId) {
                            for (raceResult in result.raceResult) {
                                competitors.add(
                                    PersonCompetitor(
                                        raceId = raceResult.eventRaceId.content,
                                        eventClassId = classResult.eventClass.eventClassId.content,
                                        personId = person.personId,
                                        name = personConverter.convertPersonName(result.person.personName),
                                        organisation = if (result.organisation != null) organisationRepository.findByOrganisationIdAndEventorId(
                                            result.organisation.organisationId.content,
                                            eventor.eventorId
                                        ) else null,
                                        birthYear = personConverter.convertBirthYear(result.person.birthDate),
                                        nationality = result.person.nationality.country.alpha3.value,
                                        gender = personConverter.convertGender(result.person.sex),
                                        punchingUnit = null,
                                        bib = null,
                                        status = CompetitorStatus.Finished,
                                        startTime = if (raceResult.result?.startTime != null) convertStartTime(
                                            raceResult.result.startTime,
                                            eventor
                                        ) else null,
                                        finishTime = if (raceResult.result?.finishTime != null) convertFinishTime(
                                            raceResult.result.finishTime,
                                            eventor
                                        ) else null,
                                        result = Result(
                                            status = ResultStatus.valueOf(raceResult.result.competitorStatus.value),
                                            position = if (raceResult.result.resultPosition != null && raceResult.result.resultPosition.content != "0") raceResult.result.resultPosition.content.toInt() else null,
                                            time = if (raceResult.result.time != null) convertTimeSec(raceResult.result.time.content) else null,
                                            timeBehind = if (raceResult.result.timeDiff != null) convertTimeSec(
                                                raceResult.result.timeDiff.content
                                            ) else null
                                        ),
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

    private fun convertTeamMemberResults(
        eventor: Eventor,
        teamMembers: List<org.iof.eventor.TeamMemberResult>
    ): List<TeamMemberCompetitor> {
        val result: MutableList<TeamMemberCompetitor> = mutableListOf()
        for (teamMember in teamMembers) {
            result.add(
                TeamMemberCompetitor(
                    personId = teamMember.person.personId.content,
                    name = personConverter.convertPersonName(teamMember.person.personName),
                    birthYear = personConverter.convertBirthYear(teamMember.person.birthDate),
                    nationality = teamMember.person.nationality.country.alpha3.value,
                    gender = personConverter.convertGender(teamMember.person.sex),
                    punchingUnit = null,
                    leg = teamMember.leg.toInt(),
                    startTime = if (teamMember.startTime != null) convertStartTime(
                        teamMember.startTime,
                        eventor
                    ) else null,
                    finishTime = if (teamMember.finishTime != null) convertFinishTime(
                        teamMember.finishTime,
                        eventor
                    ) else null,
                    legResult = Result(
                        status = ResultStatus.valueOf(teamMember.competitorStatus.value),
                        position = if (teamMember.position != null && teamMember.position[0].value.toInt() != 0) teamMember.position[0].value.toInt() else null,
                        time = if (teamMember.time != null) convertTimeSec(teamMember.time.content) else null,
                        timeBehind = if (teamMember.timeBehind != null) teamMember.timeBehind[0].value.toInt() else null
                    ),
                    overallResult = Result(
                        status = ResultStatus.valueOf(teamMember.overallResult.teamStatus.value),
                        position = if (teamMember.overallResult?.resultPosition != null && teamMember.overallResult.resultPosition.content != "0") teamMember.overallResult.resultPosition.content.toInt() else null,
                        time = if (teamMember.overallResult.time != null) convertTimeSec(teamMember.overallResult.time.content) else null,
                        timeBehind = if (teamMember.overallResult.timeDiff != null) convertTimeSec(teamMember.overallResult.timeDiff.content) else null
                    ),
                    splitTimes = listOf(),
                    entryFeeIds = listOf()
                )
            )
        }
        return result
    }

    fun generateCompetitors(
        eventor: Eventor,
        startListList: org.iof.eventor.StartListList,
        person: Person
    ): List<Competitor> {
        val competitors: MutableList<Competitor> = mutableListOf()
        for (startList in startListList.startList) {
            if (startList.event.eventRace.size == 1) {
                val race = startList.event.eventRace[0]
                for (classStart in startList.classStart) {
                    for (start in classStart.personStartOrTeamStart) {
                        if (start is org.iof.eventor.PersonStart && start.person.personId.content == person.personId) {
                            competitors.add(
                                PersonCompetitor(
                                    raceId = race.eventRaceId.content,
                                    eventClassId = classStart.eventClass.eventClassId.content,
                                    personId = person.personId,
                                    name = personConverter.convertPersonName(start.person.personName),
                                    organisation = if (start.organisation != null) organisationRepository.findByOrganisationIdAndEventorId(
                                        start.organisation.organisationId.content,
                                        eventor.eventorId
                                    ) else null,
                                    birthYear = personConverter.convertBirthYear(start.person.birthDate),
                                    nationality = start.person.nationality.country.alpha3.value,
                                    gender = personConverter.convertGender(start.person.sex),
                                    punchingUnit = null,
                                    bib = null,
                                    status = CompetitorStatus.SignedUp,
                                    startTime = if (start.start.startTime != null) convertStartTime(
                                        start.start.startTime,
                                        eventor
                                    ) else null,
                                    finishTime = null,
                                    result = null,
                                    splitTimes = listOf(),
                                    entryFeeIds = listOf()
                                )
                            )
                        } else if (start is org.iof.eventor.TeamStart) {
                            competitors.add(
                                TeamCompetitor(
                                    raceId = race.eventRaceId.content,
                                    eventClassId = classStart.eventClass.eventClassId.content,
                                    name = start.teamName.content,
                                    organisations = convertOrganisations(
                                        start.organisationIdOrOrganisationOrCountryId,
                                        eventor
                                    ),
                                    bib = null,
                                    status = CompetitorStatus.SignedUp,
                                    startTime = if (start.startTime != null) convertStartTime(
                                        start.startTime,
                                        eventor
                                    ) else null,
                                    finishTime = null,
                                    result = null,
                                    teamMembers = convertTeamMemberStarts(eventor, start.teamMemberStart)
                                )
                            )
                        }

                    }
                }
            } else {
                for (classStart in startList.classStart) {
                    for (start in classStart.personStartOrTeamStart) {
                        if (start is org.iof.eventor.PersonStart && start.person.personId.content == person.personId) {
                            for (raceStart in start.raceStart) {
                                competitors.add(
                                    PersonCompetitor(
                                        raceId = raceStart.eventRaceId.content,
                                        eventClassId = classStart.eventClass.eventClassId.content,
                                        personId = person.personId,
                                        name = personConverter.convertPersonName(start.person.personName),
                                        organisation = if (start.organisation != null) organisationRepository.findByOrganisationIdAndEventorId(
                                            start.organisation.organisationId.content,
                                            eventor.eventorId
                                        ) else null,
                                        birthYear = personConverter.convertBirthYear(start.person.birthDate),
                                        nationality = start.person.nationality.country.alpha3.value,
                                        gender = personConverter.convertGender(start.person.sex),
                                        punchingUnit = null,
                                        bib = null,
                                        status = CompetitorStatus.SignedUp,
                                        startTime = if (start.start.startTime != null) convertStartTime(
                                            start.start.startTime,
                                            eventor
                                        ) else null,
                                        finishTime = null,
                                        result = null,
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

    private fun convertTeamMemberStarts(
        eventor: Eventor,
        teamMembers: List<org.iof.eventor.TeamMemberStart>
    ): List<TeamMemberCompetitor> {
        val result: MutableList<TeamMemberCompetitor> = mutableListOf()
        for (teamMember in teamMembers) {
            result.add(
                TeamMemberCompetitor(
                    personId = teamMember.person.personId.content,
                    name = personConverter.convertPersonName(teamMember.person.personName),
                    birthYear = personConverter.convertBirthYear(teamMember.person.birthDate),
                    nationality = teamMember.person.nationality.country.alpha3.value,
                    gender = personConverter.convertGender(teamMember.person.sex),
                    punchingUnit = null,
                    leg = teamMember.leg.toInt(),
                    startTime = if (teamMember.startTime != null) convertStartTime(
                        teamMember.startTime,
                        eventor
                    ) else null,
                    finishTime = null,
                    legResult = null,
                    overallResult = null,
                    splitTimes = listOf()
                )
            )
        }
        return result
    }

    fun generateCompetitors(
        eventor: Eventor,
        entryList: org.iof.eventor.EntryList,
        person: Person
    ): Collection<Competitor> {
        val competitors: MutableList<Competitor> = mutableListOf()

        for (entry in entryList.entry) {
            if (entry.competitor?.person?.personId?.content == person.personId || entry.competitor?.personId?.content == person.personId) {
                for (eventRaceId in entry.eventRaceId) {
                    competitors.add(
                        PersonCompetitor(
                            raceId = eventRaceId.content,
                            eventClassId = entry.entryClass[0].eventClassId.content,
                            personId = person.personId,
                            name = person.name,
                            organisation = if (!entry.organisationId.isNullOrEmpty()) organisationRepository.findByOrganisationIdAndEventorId(
                                entry.organisationId[0].content,
                                eventor.eventorId
                            ) else null,
                            birthYear = person.birthYear,
                            nationality = person.nationality,
                            gender = person.gender,
                            punchingUnit = if (!entry.competitor.cCard.isNullOrEmpty()) convertCCard(entry.competitor.cCard[0]) else null,
                            bib = if (entry.bibNumber != null) entry.bibNumber?.content else null,
                            status = CompetitorStatus.SignedUp,
                            startTime = null,
                            finishTime = null,
                            result = null,
                            splitTimes = listOf(),
                            entryFeeIds = listOf()
                        )
                    )


                }
            } else if (entry.teamCompetitor != null) {
                for (teamCompetitor in entry.teamCompetitor) {
                    if (teamCompetitor.person.personId.content == person.personId) {
                        for (race in entry.eventRaceId) {
                            TeamCompetitor(
                                raceId = race.content,
                                eventClassId = entry.entryClass[0].eventClassId.content,
                                name = entry.teamName.content,
                                organisations = convertOrganisationIds(entry.organisationId, eventor),
                                bib = entry.bibNumber.content,
                                status = CompetitorStatus.SignedUp,
                                startTime = null,
                                finishTime = null,
                                result = null,
                                teamMembers = convertTeamMemberEntries(entry.teamCompetitor)
                            )
                        }
                    }
                }
            }
        }
        return competitors
    }

    private fun convertTeamMemberEntries(teamMembers: List<org.iof.eventor.TeamCompetitor>): List<TeamMemberCompetitor> {
        val result: MutableList<TeamMemberCompetitor> = mutableListOf()
        for (teamMember in teamMembers) {
            result.add(
                TeamMemberCompetitor(
                    personId = teamMember.person.personId.content,
                    name = personConverter.convertPersonName(teamMember.person.personName),
                    birthYear = personConverter.convertBirthYear(teamMember.person.birthDate),
                    nationality = teamMember.person.nationality.country.alpha3.value,
                    gender = personConverter.convertGender(teamMember.person.sex),
                    punchingUnit = if (!teamMember.cCard.isNullOrEmpty()) convertCCard(teamMember.cCard[0]) else null,
                    leg = teamMember.teamSequence.content.toInt(),
                    entryFeeIds = listOf(),
                    startTime = null,
                    finishTime = null,
                    legResult = null,
                    overallResult = null,
                    splitTimes = listOf()
                )
            )
        }
        return result
    }

    private fun convertOrganisationIds(
        organisationIds: List<org.iof.eventor.OrganisationId>,
        eventor: Eventor
    ): List<Organisation> {
        val organisations: MutableList<Organisation> = ArrayList()

        for (o in organisationIds) {
            organisationRepository.findByOrganisationIdAndEventorId(
                organisationId = o.content,
                eventorId = eventor.eventorId
            )?.let { organisations.add(it) }
        }
        return organisations

    }

    fun convertTimeSec(time: String?): Int {
        var date: Date
        var reference: Date
        try {
            val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
            reference = dateFormat.parse("00:00:00")
            date = dateFormat.parse(time)
            val seconds = (date.time - reference.time) / 1000L
            return seconds.toInt()
        } catch (e: ParseException) {
            val dateFormat: DateFormat = SimpleDateFormat("mm:ss")
            reference = dateFormat.parse("00:00:00")
            date = dateFormat.parse(time)
            val seconds = (date.time - reference.time) / 1000L
            return seconds.toInt()
        }

    }

    fun convertStartTime(time: org.iof.eventor.StartTime, eventor: Eventor): Timestamp {
        val timeString = time.date.content + " " + time.clock.content
        val zdt = parseTimestamp(timeString, eventor)
        return Timestamp.ofTimeSecondsAndNanos(zdt.toInstant().epochSecond, 0)
    }

    fun convertFinishTime(time: org.iof.eventor.FinishTime, eventor: Eventor): Timestamp {
        val timeString = time.date.content + " " + time.clock.content
        val zdt = parseTimestamp(timeString, eventor)
        return Timestamp.ofTimeSecondsAndNanos(zdt.toInstant().epochSecond, 0)
    }

    private fun parseTimestamp(time: String, eventor: Eventor): ZonedDateTime {
        val sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return ZonedDateTime.parse(time, sdf.withZone(getTimeZone(eventor)))

    }

    private fun getTimeZone(eventor: Eventor): ZoneId {
        if (eventor.eventorId == "AUS") {
            return ZoneId.of("Australia/Sydney")
        }
        return ZoneId.of("Europe/Paris")
    }


    fun convertCCard(cCard: org.iof.eventor.CCard): PunchingUnit {
        return PunchingUnit(cCard.cCardId.content, convertPunchingUnitType(cCard.punchingUnitType.value))
    }

    fun convertPunchingUnitType(value: String): PunchingUnitType {
        return when (value) {
            "manual" -> PunchingUnitType.Manual
            "Emit" -> PunchingUnitType.Emit
            "SI" -> PunchingUnitType.SI
            "emiTag" -> PunchingUnitType.EmiTag
            else -> PunchingUnitType.Other
        }

    }

    private fun convertOrganisations(organisationList: List<Any>, eventor: Eventor): List<Organisation> {
        val organisations: MutableList<Organisation> = ArrayList()

        for (o in organisationList) {
            if (o is org.iof.eventor.Organisation) {
                organisationRepository.findByOrganisationIdAndEventorId(
                    organisationId = o.organisationId.content,
                    eventorId = eventor.eventorId
                )?.let { organisations.add(it) }
            }

        }
        return organisations

    }
}