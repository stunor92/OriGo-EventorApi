package no.stunor.origo.eventorapi.services.converter

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.competitor.*
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.model.event.competitor.CompetitorStatus
import no.stunor.origo.eventorapi.model.event.competitor.Result
import no.stunor.origo.eventorapi.model.event.competitor.SplitTime
import no.stunor.origo.eventorapi.model.event.competitor.TeamCompetitor
import no.stunor.origo.eventorapi.model.organisation.SimpleOrganisation
import org.iof.eventor.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date

@Component
class ResultListConverter {

    @Autowired
    private lateinit var personConverter: PersonConverter

    @Autowired
    private lateinit var organisationConverter: OrganisationConverter

    @Throws(NumberFormatException::class, ParseException::class)
    fun convertEventResultList(resultList: ResultList, eventor: Eventor): List<Competitor> {
        val competitorList: MutableList<Competitor> = mutableListOf()


        for (classResult in resultList.classResult) {
            for (personOrTeamResult in classResult.personResultOrTeamResult) {
                if (personOrTeamResult is PersonResult) {
                    if (personOrTeamResult.raceResult != null && personOrTeamResult.raceResult.isNotEmpty()) {
                        for (raceResult in personOrTeamResult.raceResult) {
                            competitorList.add(
                                convertMultiDayPersonResult(
                                    classResult,
                                    personOrTeamResult,
                                    raceResult,
                                    eventor
                                )
                            )
                        }
                    } else {
                        competitorList.add(
                            convertOneDayPersonResult(
                                resultList.event,
                                classResult,
                                personOrTeamResult,
                                eventor
                            )
                        )
                    }
                } else if (personOrTeamResult is TeamResult) {
                    competitorList.add(convertTeamResult(resultList.event, classResult, personOrTeamResult, eventor))
                }
            }
        }

        return competitorList
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertOneDayPersonResult(
        event: Event,
        classResult: ClassResult,
        personResult: PersonResult,
        eventor: Eventor
    ): PersonCompetitor {
        return PersonCompetitor(
            raceId = event.eventRace[0].eventRaceId.content,
            eventClassId = classResult.eventClass.eventClassId.content,
            personId = if (personResult.person.personId != null) personResult.person.personId.content else null,
            name = personConverter.convertPersonName(personResult.person.personName),
            organisation = organisationConverter.convertOrganisation(personResult.organisation, eventor),
            birthYear = if (personResult.person.birthDate != null) personResult.person.birthDate.date.content.substring(
                0,
                4
            ).toInt() else null,
            nationality = if (personResult.person.nationality != null) personResult.person.nationality.country.alpha3.value else null,
            gender = personConverter.convertGender(personResult.person.sex),
            punchingUnit = null,
            bib = if (personResult.result.bibNumber != null) personResult.result.bibNumber.content else null,
            startTime = if (personResult.result.startTime != null) convertStartTime(personResult.result.startTime) else null,
            finishTime = if (personResult.result.finishTime != null) convertFinishTime(personResult.result.finishTime) else null,
            result = convertPersonResult(personResult.result),
            splitTimes = convertSplitTimes(personResult.result.splitTime),
            entryFeeIds = listOf(),
            status = CompetitorStatus.Finished
        )
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertMultiDayPersonResult(
        classResult: ClassResult,
        personResult: PersonResult,
        raceResult: RaceResult,
        eventor: Eventor
    ): Competitor {
        return PersonCompetitor(
            raceId = raceResult.eventRaceId.content,
            eventClassId = classResult.eventClass.eventClassId.content,
            personId = if (personResult.person.personId != null) personResult.person.personId.content else null,
            name = personConverter.convertPersonName(personResult.person.personName),
            organisation = organisationConverter.convertOrganisation(personResult.organisation, eventor),
            birthYear = if (personResult.person.birthDate != null) personResult.person.birthDate.date.content.substring(
                0,
                4
            ).toInt() else null,
            nationality = if (personResult.person.nationality != null) personResult.person.nationality.country.alpha3.value else null,
            gender = personConverter.convertGender(personResult.person.sex),
            punchingUnit = null,
            bib = if (personResult.result?.bibNumber != null) personResult.result.bibNumber.content else null,
            startTime = if (personResult.result?.startTime != null) convertStartTime(raceResult.result.startTime) else null,
            finishTime = if (raceResult.result?.finishTime != null) convertFinishTime(raceResult.result.finishTime) else null,
            result = convertPersonResult(raceResult.result),
            splitTimes = convertSplitTimes(raceResult.result?.splitTime),
            entryFeeIds = listOf(),
            status = CompetitorStatus.Finished
        )
    }

    @Throws(NumberFormatException::class, ParseException::class)
    fun convertPersonResult(result: org.iof.eventor.Result?): Result? {
        if (result == null)
            return null
        return Result(
            if (result.time != null) convertTimeSec(result.time.content) else null,
            if (result.timeDiff != null) convertTimeSec(result.timeDiff.content) else null,
            if (result.resultPosition != null && result.resultPosition.content != "0") result.resultPosition.content.toInt() else null,
            ResultStatus.valueOf(result.competitorStatus.value)
        )
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertTeamResult(
        event: Event,
        classResult: ClassResult,
        teamResult: TeamResult,
        eventor: Eventor
    ): Competitor {
        val organisations: MutableList<SimpleOrganisation> = ArrayList()
        for (organisation in teamResult.organisationIdOrOrganisationOrCountryId) {
            if (organisation is Organisation) {
                organisationConverter.convertOrganisation(organisation, eventor)?.let { organisations.add(it) }
            }
        }
        return TeamCompetitor(
            raceId = event.eventRace[0].eventRaceId.content,
            eventClassId = classResult.eventClass.eventClassId.content,
            name = teamResult.teamName.content,
            organisations = organisations,
            teamMembers = convertTeamMembers(teamResult.teamMemberResult),
            bib = if (teamResult.bibNumber != null) teamResult.bibNumber.content else null,
            startTime = if (teamResult.startTime != null) convertStartTime(teamResult.startTime) else null,
            finishTime = if (teamResult.finishTime != null) convertFinishTime(teamResult.finishTime) else null,
            result = convertTeamResult(teamResult),
            status = CompetitorStatus.Finished
        )
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertSplitTimes(splitTimes: List<org.iof.eventor.SplitTime>?): List<SplitTime> {
        if (splitTimes == null)
            return listOf()
        val result: MutableList<SplitTime> = ArrayList()
        for (splitTime in splitTimes) {
            result.add(convertSplitTime(splitTime))
        }
        return result
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertSplitTime(splitTime: org.iof.eventor.SplitTime): SplitTime {
        return SplitTime(
            splitTime.sequence.toInt(),
            splitTime.controlCode.content,
            if (splitTime.time != null) convertTimeSec(splitTime.time.content) else null
        )
    }

    @Throws(ParseException::class)
    fun convertTeamMembers(teamMembers: List<TeamMemberResult>): List<TeamMemberCompetitor> {
        val result: MutableList<TeamMemberCompetitor> = mutableListOf()
        for (teamMember in teamMembers) {
            result.add(convertTeamMember(teamMember))
        }
        return result
    }

    @Throws(ParseException::class)
    private fun convertTeamMember(teamMember: TeamMemberResult): TeamMemberCompetitor {
        return TeamMemberCompetitor(
            personId = if (teamMember.person != null && teamMember.person.personId != null) teamMember.person.personId.content else null,
            name = if (teamMember.person != null) personConverter.convertPersonName(teamMember.person.personName) else null,
            birthYear = if (teamMember.person != null && teamMember.person.birthDate != null) teamMember.person.birthDate.date.content.substring(
                0,
                4
            ).toInt() else null,
            nationality = if (teamMember.person != null && teamMember.person.nationality != null) teamMember.person.nationality.country.alpha3.value else null,
            gender = if (teamMember.person != null) personConverter.convertGender(teamMember.person.sex) else null,
            punchingUnit = null,
            leg = teamMember.leg.toInt(),
            entryFeeIds = listOf(),
            startTime = if (teamMember.startTime != null) convertStartTime(teamMember.startTime) else null,
            finishTime = if (teamMember.finishTime != null) convertFinishTime(teamMember.finishTime) else null,
            legResult = convertLegResult(teamMember),
            overallResult = if (teamMember.overallResult != null) convertOverallResult(teamMember.overallResult) else null,
            splitTimes = convertSplitTimes(teamMember.splitTime)
        )
    }

    @Throws(NumberFormatException::class, ParseException::class)
    fun convertTeamResult(teamResult: TeamResult): Result {
        return Result(
            if (teamResult.time != null) convertTimeSec(teamResult.time.content) else null,
            if (teamResult.timeDiff != null) convertTimeSec(teamResult.timeDiff.content) else null,
            if (teamResult.resultPosition != null && teamResult.resultPosition.content != "0") teamResult.resultPosition.content.toInt() else null,
            ResultStatus.valueOf(teamResult.teamStatus.value)
        )
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertOverallResult(overallResult: OverallResult): Result {
        return Result(
            if (overallResult.time != null) convertTimeSec(overallResult.time.content) else null,
            if (overallResult.timeDiff != null) convertTimeSec(overallResult.timeDiff.content) else null,
            if (overallResult.resultPosition != null && overallResult.resultPosition.content != "0") overallResult.resultPosition.content.toInt() else null,
            ResultStatus.valueOf(overallResult.teamStatus.value)
        )
    }

    @Throws(ParseException::class)
    private fun convertLegResult(teamMember: TeamMemberResult): Result {
        return Result(
            if (teamMember.time != null) convertTimeSec(teamMember.time.content) else null,
            if (teamMember.timeBehind != null) getTimeBehind(teamMember.timeBehind) else null,
            if (teamMember.position != null) getLegPosition(teamMember.position) else null,
            ResultStatus.valueOf(teamMember.competitorStatus.value)
        )
    }

    private fun getLegPosition(positionList: List<TeamMemberResult.Position>): Int? {
        for (position in positionList) {
            if (position.type == "Leg" && position.value.toInt() > 0) {
                return position.value.toInt()
            }
        }
        return null
    }

    @Throws(ParseException::class)
    fun convertTimeSec(time: String?): Int {
        var date: Date
        var reference: Date
        try {
            val dateFormat: DateFormat = SimpleDateFormat("HH:mm:ss")
            reference = dateFormat.parse("00:00:00")
            date = dateFormat.parse(time)
        } catch (e: ParseException) {
            val dateFormat: DateFormat = SimpleDateFormat("mm:ss")
            reference = dateFormat.parse("00:00")
            date = dateFormat.parse(time)
        }
        val seconds = (date.time - reference.time) / 1000L
        return seconds.toInt()
    }


    private fun getTimeBehind(timeBehindList: List<TeamMemberResult.TimeBehind>): Int? {
        for (timeBehind in timeBehindList) {
            if (timeBehind.type == "Leg") {
                return timeBehind.value.toInt()
            }
        }
        return null
    }

    private fun convertFinishTime(finishTime: FinishTime): Timestamp? {
        val timeString = finishTime.date.content + "T" + finishTime.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }

    private fun convertStartTime(startTime: StartTime): Timestamp? {
        val timeString = startTime.date.content + "T" + startTime.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }
}
