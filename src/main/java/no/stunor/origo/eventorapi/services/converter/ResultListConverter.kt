package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.origo.result.PersonResult
import no.stunor.origo.eventorapi.model.origo.result.RaceResultList
import no.stunor.origo.eventorapi.model.origo.Result
import no.stunor.origo.eventorapi.model.origo.result.SplitTime
import no.stunor.origo.eventorapi.model.origo.result.TeamMemberResult
import no.stunor.origo.eventorapi.model.origo.result.TeamResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Component
class ResultListConverter {
    @Autowired
    private lateinit var personConverter: PersonConverter
    @Autowired
    private lateinit var startListConverter: StartListConverter
    @Autowired
    private lateinit var organisationRepository: OrganisationRepository

    @Throws(NumberFormatException::class, ParseException::class)
    fun convertEventResultList(resultList: org.iof.eventor.ResultList, eventor: Eventor): List<RaceResultList> {
        val raceResultListMap: MutableMap<String, RaceResultList> = HashMap()

        for (eventRace in resultList.event.eventRace) {
            val raceId = eventRace.eventRaceId.content
            raceResultListMap[raceId] = RaceResultList(raceId, ArrayList(), ArrayList())
        }

        for (classResult in resultList.classResult) {
            for (personOrTeamResult in classResult.personResultOrTeamResult) {
                if (personOrTeamResult is org.iof.eventor.PersonResult) {
                    if (personOrTeamResult.raceResult != null && personOrTeamResult.raceResult.isNotEmpty()) {
                        for (raceResult in personOrTeamResult.raceResult) {
                            raceResultListMap[raceResult.eventRaceId.content]
                                    ?.personResultList
                                    ?.add(convertMultiDayPersonResult(classResult, personOrTeamResult, raceResult, eventor))
                        }
                    } else {
                        raceResultListMap[resultList.event.eventRace[0].eventRaceId.content]
                                ?.personResultList
                                ?.add(convertOneDayPersonResult(classResult, personOrTeamResult, eventor))
                    }
                } else if (personOrTeamResult is org.iof.eventor.TeamResult) {
                    raceResultListMap[resultList.event.eventRace[0].eventRaceId.content]
                            ?.teamResultList
                            ?.add(convertTeamResult(classResult, personOrTeamResult, eventor))
                }
            }
        }

        return raceResultListMap.values.stream().toList()
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertOneDayPersonResult(classResult: org.iof.eventor.ClassResult, personResult: org.iof.eventor.PersonResult, eventor: Eventor): PersonResult {
        return PersonResult(
                personResult.result.resultId.content,
                personConverter.convertCompetitor(personResult.person, eventor),
                if (personResult.organisation != null && personResult.organisation.organisationId != null) organisationRepository.findByOrganisationIdAndEventorId(personResult.organisation.organisationId.content, eventor.eventorId).block() else null,
                if (personResult.result.startTime != null) startListConverter.convertStartTime(personResult.result.startTime) else null,
                if (personResult.result.finishTime != null) convertFinishTime(personResult.result.finishTime) else null,
                convertPersonResult(personResult.result),
                convertSplitTimes(personResult.result.splitTime),
                if (personResult.result.bibNumber != null) personResult.result.bibNumber.content else "",
                classResult.eventClass.eventClassId.content)
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertMultiDayPersonResult(classResult: org.iof.eventor.ClassResult, personResult: org.iof.eventor.PersonResult, raceResult: org.iof.eventor.RaceResult, eventor: Eventor): PersonResult {
        return PersonResult(
                raceResult.eventRaceId.content,
                personConverter.convertCompetitor(personResult.person, eventor),
                if (personResult.organisation != null && personResult.organisation.organisationId != null) organisationRepository.findByOrganisationIdAndEventorId(personResult.organisation.organisationId.content, eventor.eventorId).block() else null,
                if (raceResult.result.startTime != null) startListConverter.convertStartTime(raceResult.result.startTime) else null,
                if (raceResult.result.finishTime != null) convertFinishTime(raceResult.result.finishTime) else null,
                convertPersonResult(raceResult.result),
                convertSplitTimes(raceResult.result.splitTime),
                if (raceResult.result.bibNumber != null) raceResult.result.bibNumber.content else "",
                classResult.eventClass.eventClassId.content)
    }

    @Throws(NumberFormatException::class, ParseException::class)
    fun convertPersonResult(result: org.iof.eventor.Result): Result {
        return Result(
                if (result.time != null) convertTimeSec(result.time.content) else null,
                if (result.timeDiff != null) convertTimeSec(result.timeDiff.content) else null,
                if (result.resultPosition != null && result.resultPosition.content != "0") result.resultPosition.content.toInt() else null,
                result.competitorStatus.value)
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertTeamResult(classResult: org.iof.eventor.ClassResult, teamResult: org.iof.eventor.TeamResult, eventor: Eventor): TeamResult {
        val organisations: MutableList<Organisation> = ArrayList()
        for (organisation  in teamResult.organisationIdOrOrganisationOrCountryId) {
            if(organisation is org.iof.eventor.Organisation) {
                organisationRepository.findByOrganisationIdAndEventorId(organisation.organisationId.content, eventor.eventorId).block()?.let { organisations.add(it) }
            }
        }

        return TeamResult(
                "",
                organisations,
                convertTeamMembers(teamResult.teamMemberResult, eventor),
                teamResult.teamName.content,
                if (teamResult.startTime != null) startListConverter.convertStartTime(teamResult.startTime) else null,
                if (teamResult.finishTime != null) convertFinishTime(teamResult.finishTime) else null,
                convertTeamResult(teamResult),
                if (teamResult.bibNumber != null) teamResult.bibNumber.content else null,
                classResult.eventClass.eventClassId.content)
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertSplitTimes(splitTimes: List<org.iof.eventor.SplitTime>): List<SplitTime> {
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
                if (splitTime.time != null) convertTimeSec(splitTime.time.content) else null)
    }

    @Throws(ParseException::class)
    fun convertTeamMembers(teamMembers: List<org.iof.eventor.TeamMemberResult>, eventor: Eventor): List<TeamMemberResult> {
        val result: MutableList<TeamMemberResult> = ArrayList()
        for (teamMember in teamMembers) {
            result.add(convertTeamMember(teamMember, eventor))
        }
        return result
    }

    @Throws(ParseException::class)
    private fun convertTeamMember(teamMember: org.iof.eventor.TeamMemberResult, eventor: Eventor): TeamMemberResult {
        return TeamMemberResult(
                if (teamMember.person != null) personConverter.convertCompetitor(teamMember.person, eventor) else null,
                teamMember.leg.toInt(),
                if (teamMember.startTime != null) startListConverter.convertStartTime(teamMember.startTime) else null,
                if (teamMember.finishTime != null) convertFinishTime(teamMember.finishTime) else null,
                convertLegResult(teamMember),
                if (teamMember.overallResult != null) convertOverallResult(teamMember.overallResult) else null,
                convertSplitTimes(teamMember.splitTime))
    }

    @Throws(NumberFormatException::class, ParseException::class)
    fun convertTeamResult(teamResult: org.iof.eventor.TeamResult): Result {
        return Result(
                if (teamResult.time != null) convertTimeSec(teamResult.time.content) else null,
                if (teamResult.timeDiff != null) convertTimeSec(teamResult.timeDiff.content) else null,
                if (teamResult.resultPosition != null && teamResult.resultPosition.content != "0") teamResult.resultPosition.content.toInt() else null,
                teamResult.teamStatus.value)
    }

    @Throws(NumberFormatException::class, ParseException::class)
    private fun convertOverallResult(overallResult: org.iof.eventor.OverallResult): Result {
        return Result(
                if (overallResult.time != null) convertTimeSec(overallResult.time.content) else null,
                if (overallResult.timeDiff != null) convertTimeSec(overallResult.timeDiff.content) else null,
                if (overallResult.resultPosition != null && overallResult.resultPosition.content != "0") overallResult.resultPosition.content.toInt() else null,
                overallResult.teamStatus.value)
    }

    @Throws(ParseException::class)
    private fun convertLegResult(teamMember: org.iof.eventor.TeamMemberResult): Result {
        return Result(
                if (teamMember.time != null) convertTimeSec(teamMember.time.content) else null,
                if (teamMember.timeBehind != null) getTimeBehind(teamMember.timeBehind) else null,
                if (teamMember.position != null) getLegPosition(teamMember.position) else null,
                teamMember.competitorStatus.value)
    }

    private fun getLegPosition(positionList: List<org.iof.eventor.TeamMemberResult.Position>): Int? {
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


    private fun getTimeBehind(timeBehindList: List<org.iof.eventor.TeamMemberResult.TimeBehind>): Int? {
        for (timeBehind in timeBehindList) {
            if (timeBehind.type == "Leg") {
                return timeBehind.value.toInt()
            }
        }
        return null
    }

    private fun convertFinishTime(finishTime: org.iof.eventor.FinishTime): Date? {
        val dateString = finishTime.date.content + " " + finishTime.clock.content
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        parser.timeZone = TimeZone.getTimeZone("UTC")

        return try {
            parser.parse(dateString)
        } catch (e: ParseException) {
            null
        }
    }
}
