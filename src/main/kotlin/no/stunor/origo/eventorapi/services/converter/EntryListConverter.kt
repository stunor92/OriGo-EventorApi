package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import no.stunor.origo.eventorapi.model.event.competitor.CompetitorStatus
import no.stunor.origo.eventorapi.model.event.competitor.PersonCompetitor
import no.stunor.origo.eventorapi.model.event.competitor.TeamCompetitor
import no.stunor.origo.eventorapi.model.event.competitor.TeamMemberCompetitor
import org.iof.eventor.Entry
import org.iof.eventor.EntryList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EntryListConverter {
    @Autowired
    private lateinit var personConverter: PersonConverter

    @Autowired
    private lateinit var competitorConverter: CompetitorConverter

    @Autowired
    private lateinit var organisationConverter: OrganisationConverter

    @Autowired
    private lateinit var entryConverter: EntryConverter

    fun convertEventEntryList(entryList: EntryList): List<Competitor> {
        val result  = mutableListOf<Competitor>()

        for (entry in entryList.entry) {
            if (entry.competitor != null) {
                result.addAll(convertPersonEventEntries(entry))
            } else if (entry.teamCompetitor != null) {
                result.addAll(convertTeamEventEntries(entry))
            }
        }
        return result
    }

    private fun convertPersonEventEntries(entry: Entry): List<Competitor> {
        val result  = mutableListOf<Competitor>()

        for (raceId in entry.eventRaceId) {
            result.add(
                PersonCompetitor(
                    raceId = raceId.content,
                    classId = entry.entryClass[0].eventClassId.content,
                    personId = if (entry.competitor.person.personId != null) entry.competitor.person.personId.content else null,
                    name = personConverter.convertPersonName(entry.competitor.person.personName),
                    organisationId = if(entry.competitor.organisation != null) organisationConverter.convertOrganisationId(entry.competitor.organisation) else organisationConverter.convertOrganisationId(entry.competitor.organisationId),
                    birthYear = if (entry.competitor.person.birthDate != null) entry.competitor.person.birthDate.date.content.substring(
                        0,
                        4
                    ).toInt() else null,
                    nationality = if (entry.competitor.person.nationality?.country != null) entry.competitor.person.nationality.country.alpha3.value else null,
                    gender = personConverter.convertGender(entry.competitor.person.sex),
                    punchingUnits = competitorConverter.convertPunchingUnits(entry.competitor.cCard),
                    bib = if (entry.bibNumber != null) entry.bibNumber.content else null,
                    startTime = null,
                    finishTime = null,
                    result = null,
                    splitTimes = listOf(),
                    entryFeeIds = entryConverter.convertEntryFeesIds(
                        entry.entryEntryFee,
                        null
                    ),
                    status = CompetitorStatus.SignedUp
                )
            )
        }
        return result
    }


    private fun convertTeamEventEntries(entry: Entry): List<Competitor> {
        val result  = mutableListOf<Competitor>()

        for (race in entry.teamCompetitor[0].entryEntryFee) {
            result.add(
                TeamCompetitor(
                    raceId = race.eventRaceId,
                    classId = entry.entryClass[0].eventClassId.content,
                    name = entry.teamName.content,
                    organisationIds =  convertTeamOrganisationIds(entry.teamCompetitor),
                    bib = if (entry.bibNumber != null) entry.bibNumber.content else null,
                    teamMembers = convertTeamMembers(entry.teamCompetitor, race.eventRaceId),
                    startTime = null,
                    finishTime = null,
                    result = null,
                    status = CompetitorStatus.SignedUp
                )
            )

        }
        return result
    }

    private fun convertTeamOrganisationIds(
        teamCompetitors: List<org.iof.eventor.TeamCompetitor>
    ): List<String> {
        val result  = mutableListOf<String>()
        for (teamCompetitor in teamCompetitors) {
            if (teamCompetitor.organisationId != null) {
                var organisationExist = false
                for (organisationId in result) {
                    if (organisationId == teamCompetitor.organisationId.content) {
                        organisationExist = true
                    }
                }
                if (!organisationExist) {
                    result.add(teamCompetitor.organisationId.content)
                }
            }
        }
        return result
    }

    private fun convertTeamMembers(
        teamMembers: List<org.iof.eventor.TeamCompetitor>,
        raceId: String
    ): List<TeamMemberCompetitor> {
        val result  = mutableListOf<TeamMemberCompetitor>()
        for (teamMember in teamMembers) {
            result.add(convertTeamMember(teamMember, raceId))
        }
        return result
    }


    private fun convertTeamMember(teamMember: org.iof.eventor.TeamCompetitor, raceId: String): TeamMemberCompetitor {
        return TeamMemberCompetitor(
            personId = if (teamMember.person != null && teamMember.person.personId != null) teamMember.person.personId.content else null,
            name = if (teamMember.person != null) personConverter.convertPersonName(teamMember.person.personName) else null,
            birthYear = if (teamMember.person != null && teamMember.person.birthDate != null) teamMember.person.birthDate.date.content.substring(
                0,
                4
            ).toInt() else null,
            nationality = if (teamMember.person != null && teamMember.person.nationality != null) teamMember.person.nationality.country.alpha3.value else null,
            gender = if (teamMember.person != null) personConverter.convertGender(teamMember.person.sex) else null,
            punchingUnits = competitorConverter.convertPunchingUnits(teamMember.cCard),
            leg = teamMember.teamSequence.content.toInt(),
            startTime = null,
            finishTime = null,
            legResult = null,
            overallResult = null,
            splitTimes = listOf(),
            entryFeeIds = entryConverter.convertEntryFeesIds(
                teamMember.entryEntryFee,
                raceId
            )

        )
    }
}
