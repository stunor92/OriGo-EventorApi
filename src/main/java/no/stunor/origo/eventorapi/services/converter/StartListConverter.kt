package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.origo.start.PersonStart
import no.stunor.origo.eventorapi.model.origo.start.RaceStartList
import no.stunor.origo.eventorapi.model.origo.start.TeamMemberStart
import no.stunor.origo.eventorapi.model.origo.start.TeamStart
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Component
class StartListConverter {
    @Autowired
    private lateinit var personConverter: PersonConverter

    @Autowired
    private lateinit var organisationRepository: OrganisationRepository
    fun convertEventStartList(startList: org.iof.eventor.StartList, eventor: Eventor): List<RaceStartList> {
        val raceStartListMap: MutableMap<String, RaceStartList> = HashMap()

        for (eventRace in startList.event.eventRace) {
            val raceId = eventRace.eventRaceId.content
            raceStartListMap[raceId] = RaceStartList(raceId, ArrayList(), ArrayList())
        }

        for (classStart in startList.classStart) {
            for (personOrTeamStart in classStart.personStartOrTeamStart) {
                if (personOrTeamStart is org.iof.eventor.PersonStart) {
                    if (personOrTeamStart.raceStart != null && personOrTeamStart.raceStart.isNotEmpty()) {
                        for (raceStart in personOrTeamStart.raceStart) {
                            raceStartListMap[raceStart.eventRaceId.content]
                                    ?.personStartList
                                    ?.add(convertMultiDayPersonStart(classStart, personOrTeamStart, raceStart, eventor))
                        }
                    } else {
                        raceStartListMap[startList.event.eventRace[0].eventRaceId.content]
                                ?.personStartList
                                ?.add(convertOneDayPersonStart(classStart, personOrTeamStart, eventor))
                    }
                } else if (personOrTeamStart is org.iof.eventor.TeamStart) {
                    raceStartListMap[startList.event.eventRace[0].eventRaceId.content]
                            ?.teamStartList
                            ?.add(convertTeamStart(classStart, personOrTeamStart, eventor))
                }
            }
        }

        return raceStartListMap.values.stream().toList()
    }

    private fun convertMultiDayPersonStart(classStart: org.iof.eventor.ClassStart, personStart: org.iof.eventor.PersonStart, raceStart: org.iof.eventor.RaceStart, eventor: Eventor): PersonStart {
        return PersonStart(
                raceStart.start.startId.content,
                personConverter.convertCompetitor(personStart.person, eventor),
                if (personStart.organisation != null && personStart.organisation.organisationId != null) organisationRepository.findByOrganisationIdAndEventorId(personStart.organisation.organisationId.content, eventor.eventorId).block() else null,
                if (raceStart.start.startTime != null) convertStartTime(raceStart.start.startTime) else null,
                if (raceStart.start.bibNumber != null) raceStart.start.bibNumber.content else "",
                classStart.eventClass.eventClassId.content)
    }

    private fun convertOneDayPersonStart(classStart: org.iof.eventor.ClassStart, personStart: org.iof.eventor.PersonStart, eventor: Eventor): PersonStart {
        return PersonStart(
                personStart.start.startId.content,
                personConverter.convertCompetitor(personStart.person, eventor),
                if (personStart.organisation != null && personStart.organisation.organisationId != null) organisationRepository.findByOrganisationIdAndEventorId(personStart.organisation.organisationId.content, eventor.eventorId).block() else null,
                if (personStart.start.startTime != null) convertStartTime(personStart.start.startTime) else null,
                if (personStart.start.bibNumber != null) personStart.start.bibNumber.content else "",
                classStart.eventClass.eventClassId.content)
    }

    private fun convertTeamStart(classStart: org.iof.eventor.ClassStart, teamStart: org.iof.eventor.TeamStart, eventor: Eventor): TeamStart {
        val organisations: MutableList<Organisation> = ArrayList()
        for (organisation in teamStart.organisationIdOrOrganisationOrCountryId) {
            if (organisation is org.iof.eventor.Organisation) {
                organisationRepository.findByOrganisationIdAndEventorId(organisation.organisationId.content, eventor.eventorId).block()?.let { organisations.add(it) }
            }
        }
        return TeamStart(
                "",
                organisations,
                convertTeamMembers(teamStart.teamMemberStart, eventor),
                teamStart.teamName.content,
                if (teamStart.startTime != null) convertStartTime(teamStart.startTime) else null,
                if (teamStart.bibNumber != null) teamStart.bibNumber.content else "",
                classStart.eventClass.eventClassId.content)
    }

    private fun convertTeamMembers(teamMembers: List<org.iof.eventor.TeamMemberStart>, eventor: Eventor): List<TeamMemberStart> {
        val result: MutableList<TeamMemberStart> = ArrayList()
        for (teamMember in teamMembers) {
            result.add(convertTeamMember(teamMember, eventor))
        }
        return result
    }

    private fun convertTeamMember(teamMember: org.iof.eventor.TeamMemberStart, eventor: Eventor): TeamMemberStart {
        return TeamMemberStart(
                if (teamMember.person != null) personConverter.convertCompetitor(teamMember.person, eventor) else null,
                teamMember.leg.toInt(),
                if (teamMember.startTime != null) convertStartTime(teamMember.startTime) else null)
    }

    fun convertStartTime(startTime: org.iof.eventor.StartTime): Date? {
        val dateString = startTime.date.content + " " + startTime.clock.content
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        parser.timeZone = TimeZone.getTimeZone("UTC")

        return try {
            parser.parse(dateString)
        } catch (e: ParseException) {
            null
        }
    }
}