package no.stunor.origo.eventorapi.services.converter

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.model.event.competitor.PersonCompetitor
import no.stunor.origo.eventorapi.model.event.competitor.TeamCompetitor
import no.stunor.origo.eventorapi.model.event.competitor.TeamMemberCompetitor
import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StartListConverter {
    @Autowired
    private lateinit var personConverter: PersonConverter

    @Autowired
    private lateinit var organisationConverter: OrganisationConverter

    fun convertEventStartList(startList: org.iof.eventor.StartList, eventor: Eventor): List<Competitor> {
        val competitorList: MutableList<Competitor> = mutableListOf()

        for (classStart in startList.classStart) {
            for (personOrTeamStart in classStart.personStartOrTeamStart) {
                if (personOrTeamStart is org.iof.eventor.PersonStart) {
                    if (personOrTeamStart.raceStart != null && personOrTeamStart.raceStart.isNotEmpty()) {
                        for (raceStart in personOrTeamStart.raceStart) {
                            competitorList.add(convertMultiDayPersonStart(startList.event, classStart, personOrTeamStart, raceStart, eventor))
                        }
                    } else {
                        competitorList.add(convertOneDayPersonStart(startList.event, classStart, personOrTeamStart, eventor))
                    }
                } else if (personOrTeamStart is org.iof.eventor.TeamStart) {
                    competitorList.add(convertTeamStart(startList.event, classStart, personOrTeamStart, eventor))
                }
            }
        }

        return competitorList
    }

    private fun convertMultiDayPersonStart(event: org.iof.eventor.Event, classStart: org.iof.eventor.ClassStart, personStart: org.iof.eventor.PersonStart, raceStart: org.iof.eventor.RaceStart, eventor: Eventor): Competitor {
        return PersonCompetitor(
                eventorId = eventor.eventorId,
                eventId = event.eventId.content,
                raceId = raceStart.eventRaceId.content,
                eventClassId = classStart.eventClass.eventClassId.content,
                personId = if(personStart.person.personId != null) personStart.person.personId.content else null,
                name = personConverter.convertPersonName(personStart.person.personName),
                organisation = organisationConverter.convertOrganisation(personStart.organisation, eventor),
                birthYear = if(personStart.person.birthDate != null) personStart.person.birthDate.date.content.substring(0, 4).toInt() else null,
                nationality = if(personStart.person.nationality != null) personStart.person.nationality .country.alpha3.value else null,
                gender = personConverter.convertGender(personStart.person.sex),
                punchingUnit = null,
                bib = if (raceStart.start?.bibNumber != null) raceStart.start.bibNumber.content else null,
                startTime = if (raceStart.start?.startTime != null) convertStartTime(raceStart.start.startTime) else null,
                finishTime = null,
                result = null,
                splitTimes = listOf(),
                entryFeeIds = listOf()
        )
    }

    private fun convertOneDayPersonStart(event: org.iof.eventor.Event, classStart: org.iof.eventor.ClassStart, personStart: org.iof.eventor.PersonStart, eventor: Eventor): Competitor {
        return PersonCompetitor(
                eventorId = eventor.eventorId,
                eventId = event.eventId.content,
                raceId = event.eventRace[0].eventRaceId.content,
                eventClassId = classStart.eventClass.eventClassId.content,
                personId = if(personStart.person.personId != null) personStart.person.personId.content else null,
                name = personConverter.convertPersonName(personStart.person.personName),
                organisation = organisationConverter.convertOrganisation(personStart.organisation, eventor),
                birthYear = if(personStart.person.birthDate != null) personStart.person.birthDate.date.content.substring(0, 4).toInt() else null,
                nationality = if(personStart.person.nationality != null) personStart.person.nationality .country.alpha3.value else null,
                gender = personConverter.convertGender(personStart.person.sex),
                punchingUnit = null,
                bib = if (personStart.start.bibNumber != null) personStart.start.bibNumber.content else null,
                startTime = if (personStart.start.startTime != null) convertStartTime(personStart.start.startTime) else null,
                finishTime = null,
                result = null,
                splitTimes = listOf(),
                entryFeeIds = listOf()
        )
    }

    private fun convertTeamStart(event: org.iof.eventor.Event, classStart: org.iof.eventor.ClassStart, teamStart: org.iof.eventor.TeamStart, eventor: Eventor): TeamCompetitor {
        val organisations: MutableList<Organisation> = ArrayList()
        for (organisation in teamStart.organisationIdOrOrganisationOrCountryId) {
            if (organisation is org.iof.eventor.Organisation) {
                organisationConverter.convertOrganisation(organisation, eventor)?.let { organisations.add(it) }
            }
        }
        return TeamCompetitor(
                eventorId = eventor.eventorId,
                eventId = event.eventId.content,
                raceId = event.eventRace[0].eventRaceId.content,
                eventClassId = classStart.eventClass.eventClassId.content,
                name = teamStart.teamName.content,
                organisations = organisations,
                teamMembers = convertTeamMembers(teamStart.teamMemberStart),
                bib = if (teamStart.bibNumber != null) teamStart.bibNumber.content else null,
                startTime = if (teamStart.startTime != null) convertStartTime(teamStart.startTime) else null,
                finishTime = null,
                result = null,
        )

    }

    private fun convertTeamMembers(teamMembers: List<org.iof.eventor.TeamMemberStart>): List<TeamMemberCompetitor> {
        val result: MutableList<TeamMemberCompetitor> = mutableListOf()
        for (teamMember in teamMembers) {
            result.add(convertTeamMember(teamMember))
        }
        return result
    }

    private fun convertTeamMember(teamMember: org.iof.eventor.TeamMemberStart): TeamMemberCompetitor {
        return TeamMemberCompetitor(
                personId = if(teamMember.person != null &&teamMember.person.personId != null) teamMember.person.personId.content else null,
                name = if(teamMember.person != null) personConverter.convertPersonName(teamMember.person.personName) else null,
                birthYear = if(teamMember.person != null && teamMember.person.birthDate != null) teamMember.person.birthDate.date.content.substring(0, 4).toInt() else null,
                nationality = if(teamMember.person != null && teamMember.person.nationality != null) teamMember.person.nationality .country.alpha3.value else null,
                gender = if(teamMember.person != null) personConverter.convertGender(teamMember.person.sex) else null,
                punchingUnit = null,
                leg = teamMember.leg.toInt(),
                startTime = if (teamMember.startTime != null) convertStartTime(teamMember.startTime) else null,
                finishTime = null,
                legResult = null,
                overallResult = null,
                splitTimes = listOf(),
                entryFeeIds = listOf()
        )
    }

    private fun convertStartTime(startTime: org.iof.eventor.StartTime): Timestamp? {
        val timeString = startTime.date.content + "T" + startTime.clock.content + ".000Z"
        return Timestamp.parseTimestamp(timeString)
    }
}