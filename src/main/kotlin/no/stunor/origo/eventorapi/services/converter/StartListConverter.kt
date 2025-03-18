package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.model.event.competitor.CompetitorStatus
import no.stunor.origo.eventorapi.model.event.competitor.PersonCompetitor
import no.stunor.origo.eventorapi.model.event.competitor.TeamCompetitor
import no.stunor.origo.eventorapi.model.event.competitor.TeamMemberCompetitor
import org.iof.eventor.StartList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StartListConverter {
    @Autowired
    private lateinit var personConverter: PersonConverter

    @Autowired
    private lateinit var organisationConverter: OrganisationConverter

    @Autowired
    private lateinit var competitorConverter: CompetitorConverter

    fun convertEventStartList(eventor: Eventor, startList: StartList): List<Competitor> {
        val competitorList: MutableList<Competitor> = mutableListOf()

        for (classStart in startList.classStart) {
            processClassStart(
                eventor = eventor,
                startList = startList,
                classStart = classStart,
                competitorList = competitorList
            )
        }

        return competitorList
    }

    private fun processClassStart(
        eventor: Eventor,
        startList: org.iof.eventor.StartList,
        classStart: org.iof.eventor.ClassStart,
        competitorList: MutableList<Competitor>
    ) {
        for (personOrTeamStart in classStart.personStartOrTeamStart) {
            when (personOrTeamStart) {
                is org.iof.eventor.PersonStart -> processPersonStart(
                    eventor = eventor,
                    startList = startList,
                    classStart = classStart,
                    personStart = personOrTeamStart,
                    competitorList = competitorList
                )
                is org.iof.eventor.TeamStart -> competitorList.add(
                    convertTeamStart(
                        eventor = eventor,
                        event = startList.event,
                        classStart = classStart,
                        teamStart = personOrTeamStart
                    )
                )
            }
        }
    }

    private fun processPersonStart(
        eventor: Eventor,
        startList: org.iof.eventor.StartList,
        classStart: org.iof.eventor.ClassStart,
        personStart: org.iof.eventor.PersonStart,
        competitorList: MutableList<Competitor>
    ) {
        if (!personStart.raceStart.isNullOrEmpty()) {
            for (raceStart in personStart.raceStart) {
                competitorList.add(convertMultiDayPersonStart(eventor, classStart, personStart, raceStart))
            }
        } else {
            competitorList.add(convertOneDayPersonStart(eventor, startList.event, classStart, personStart))
        }
    }

    private fun convertMultiDayPersonStart(
        eventor: Eventor,
        classStart: org.iof.eventor.ClassStart,
        personStart: org.iof.eventor.PersonStart,
        raceStart: org.iof.eventor.RaceStart
    ): Competitor {
        return PersonCompetitor(
            raceId = raceStart.eventRaceId.content,
            eventClassId = classStart.eventClass.eventClassId.content,
            personId = if (personStart.person.personId != null)
                personStart.person.personId.content
            else null,
            name = personConverter.convertPersonName(personStart.person.personName),
            organisationId = if (personStart.organisation != null)
                organisationConverter.convertOrganisationId(personStart.organisation)
            else organisationConverter.convertOrganisationId(personStart.organisationId),
            birthYear = if (personStart.person.birthDate != null)
                personStart.person.birthDate.date.content.substring(YEAR_SUBSTRING_START, YEAR_SUBSTRING_END).toInt()
            else null,
            nationality = if (personStart.person.nationality != null)
                personStart.person.nationality.country.alpha3.value
            else null,
            gender = personConverter.convertGender(personStart.person.sex),
            bib = if (raceStart.start?.bibNumber != null)
                raceStart.start.bibNumber.content
            else null,
            startTime = if (raceStart.start?.startTime != null)
                competitorConverter.convertStartTime(raceStart.start.startTime, eventor)
            else null,
            finishTime = null,
            result = null,
            splitTimes = listOf(),
            status = CompetitorStatus.SignedUp
        )
    }

    private fun convertOneDayPersonStart(
        eventor: Eventor,
        event: org.iof.eventor.Event,
        classStart: org.iof.eventor.ClassStart,
        personStart: org.iof.eventor.PersonStart
    ): Competitor {
        return PersonCompetitor(
            raceId = event.eventRace[0].eventRaceId.content,
            eventClassId = classStart.eventClass.eventClassId.content,
            personId = if (personStart.person.personId != null) personStart.person.personId.content else null,
            name = personConverter.convertPersonName(personStart.person.personName),
            organisationId = if (personStart.organisation != null)
                organisationConverter.convertOrganisationId(personStart.organisation)
            else organisationConverter.convertOrganisationId(personStart.organisationId),
            birthYear = if (personStart.person.birthDate != null)
                personStart.person.birthDate.date.content.substring(YEAR_SUBSTRING_START, YEAR_SUBSTRING_END).toInt()
            else null,
            nationality = if (personStart.person.nationality != null)
                personStart.person.nationality.country.alpha3.value
            else null,
            gender = personConverter.convertGender(personStart.person.sex),
            bib = if (personStart.start.bibNumber != null)
                personStart.start.bibNumber.content
            else null,
            startTime = if (personStart.start.startTime != null)
                competitorConverter.convertStartTime(personStart.start.startTime, eventor)
            else null,
            status = CompetitorStatus.SignedUp
        )
    }

    private fun convertTeamStart(
        eventor: Eventor,
        event: org.iof.eventor.Event,
        classStart: org.iof.eventor.ClassStart,
        teamStart: org.iof.eventor.TeamStart
    ): Competitor {
        return TeamCompetitor(
            raceId = event.eventRace[0].eventRaceId.content,
            eventClassId = classStart.eventClass.eventClassId.content,
            name = teamStart.teamName.content,
            organisationIds = organisationConverter.convertOrganisationIds(
                teamStart.organisationIdOrOrganisationOrCountryId
            ),
            teamMembers = convertTeamMembers(eventor, teamStart.teamMemberStart),
            bib = if (teamStart.bibNumber != null) teamStart.bibNumber.content else null,
            startTime = if (teamStart.startTime != null) competitorConverter.convertStartTime(
                teamStart.startTime,
                eventor
            ) else null,
            status = CompetitorStatus.SignedUp
        )

    }

    private fun convertTeamMembers(
        eventor: Eventor,
        teamMembers: List<org.iof.eventor.TeamMemberStart>
    ): List<TeamMemberCompetitor> {
        val result: MutableList<TeamMemberCompetitor> = mutableListOf()
        for (teamMember in teamMembers) {
            result.add(convertTeamMember(eventor, teamMember))
        }
        return result
    }

    private fun convertTeamMember(eventor: Eventor, teamMember: org.iof.eventor.TeamMemberStart): TeamMemberCompetitor {
        return TeamMemberCompetitor(
            personId = if (teamMember.person != null && teamMember.person.personId != null)
                teamMember.person.personId.content
            else null,
            name = if (teamMember.person != null)
                personConverter.convertPersonName(teamMember.person.personName)
            else null,
            birthYear = if (teamMember.person != null && teamMember.person.birthDate != null)
                teamMember.person.birthDate.date.content.substring(YEAR_SUBSTRING_START, YEAR_SUBSTRING_END).toInt()
            else null,
            nationality = if (teamMember.person != null && teamMember.person.nationality != null)
                teamMember.person.nationality.country.alpha3.value
            else null,
            gender = if (teamMember.person != null) personConverter.convertGender(teamMember.person.sex) else null,
            leg = teamMember.leg.toInt(),
            startTime = if (teamMember.startTime != null) competitorConverter.convertStartTime(
                teamMember.startTime,
                eventor
            ) else null,
            splitTimes = listOf()
        )
    }

    companion object {
        private const val YEAR_SUBSTRING_START = 0
        private const val YEAR_SUBSTRING_END = 4
    }
}
