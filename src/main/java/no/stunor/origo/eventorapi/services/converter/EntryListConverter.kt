package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.event.entrylist.CompetitorEntry
import no.stunor.origo.eventorapi.model.event.entrylist.PersonEntry
import no.stunor.origo.eventorapi.model.event.entrylist.TeamEntry
import no.stunor.origo.eventorapi.model.event.entrylist.TeamMemberEntry
import no.stunor.origo.eventorapi.model.organisation.Organisation
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

    fun convertEventEntryList(entryList: org.iof.eventor.EntryList): List<CompetitorEntry> {
        val result: MutableList<CompetitorEntry> = mutableListOf()

        for (entry in entryList.entry) {
            if (entry.competitor != null) {
                result.addAll(convertPersonEventEntries(entry))
            } else if (entry.teamCompetitor != null) {
                result.addAll(convertTeamEventEntries(entry))
            }
        }
        return result
    }

    private fun convertPersonEventEntries(entry: org.iof.eventor.Entry): List<CompetitorEntry> {
        val result: MutableList<CompetitorEntry> = mutableListOf()

        for (raceId in entry.eventRaceId) {
            result.add(
                PersonEntry(
                    raceId = raceId.content,
                    eventClassId = entry.entryClass[0].eventClassId.content,
                    personId = if (entry.competitor.person.personId != null) entry.competitor.person.personId.content else null,
                    name = personConverter.convertPersonName(entry.competitor.person.personName),
                    organisation = organisationConverter.convertOrganisation(entry.competitor.organisation),
                    birthYear = if (entry.competitor.person.birthDate != null) entry.competitor.person.birthDate.date.content.substring(
                        0,
                        4
                    ).toInt() else null,
                    nationality = if (entry.competitor.person.nationality?.country != null) entry.competitor.person.nationality.country.alpha3.value else null,
                    gender = personConverter.convertGender(entry.competitor.person.sex),
                    punchingUnit = if (entry.competitor.cCard != null && entry.competitor.cCard.isNotEmpty()) competitorConverter.convertCCard(
                        entry.competitor.cCard[0]
                    ) else null,
                    entryFeeIds = if (entry.entryEntryFee != null) convertEntryFees(
                        entry.entryEntryFee,
                        null
                    ) else listOf()
                )
            )
        }
        return result
    }


    private fun convertTeamEventEntries(entry: org.iof.eventor.Entry): List<CompetitorEntry> {
        val result: MutableList<CompetitorEntry> = mutableListOf()

        for (race in entry.teamCompetitor[0].entryEntryFee) {
            result.add(
                TeamEntry(
                    raceId = race.eventRaceId,
                    eventClassId = entry.entryClass[0].eventClassId.content,
                    name = entry.teamName.content,
                    organisations = convertTeamOrganisations(entry.teamCompetitor),
                    teamMembers = convertTeamMembers(entry.teamCompetitor, race.eventRaceId)
                )
            )

        }
        return result
    }

    private fun convertTeamOrganisations(teamCompetitors: List<org.iof.eventor.TeamCompetitor>): List<Organisation> {
        val result: MutableList<Organisation> = mutableListOf()
        for (teamCompetitor in teamCompetitors) {
            if (teamCompetitor.organisationId != null) {
                var organisationExist = false
                for ((organisationId) in result) {
                    if (organisationId == teamCompetitor.organisationId.content) {
                        organisationExist = true
                    }
                }
                if (!organisationExist) {
                    organisationConverter.convertOrganisation(teamCompetitor.organisation)?.let { result.add(it) }
                }
            }
        }
        return result
    }

    private fun convertTeamMembers(
        teamMembers: List<org.iof.eventor.TeamCompetitor>,
        raceId: String
    ): List<TeamMemberEntry> {
        val result: MutableList<TeamMemberEntry> = ArrayList()
        for (teamMember in teamMembers) {
            result.add(convertTeamMember(teamMember, raceId))
        }
        return result
    }

    private fun convertEntryFees(entryFees: List<org.iof.eventor.EntryEntryFee>, raceId: String?): List<String> {
        val result: MutableList<String> = ArrayList()
        for (entryFee in entryFees) {
            if (raceId == null || entryFee.eventRaceId == raceId)
                result.add(entryFee.entryFeeId.content)
        }
        return result
    }

    private fun convertTeamMember(teamMember: org.iof.eventor.TeamCompetitor, raceId: String): TeamMemberEntry {
        return TeamMemberEntry(
            personId = if (teamMember.person != null && teamMember.person.personId != null) teamMember.person.personId.content else null,
            name = if (teamMember.person != null) personConverter.convertPersonName(teamMember.person.personName) else null,
            birthYear = if (teamMember.person != null && teamMember.person.birthDate != null) teamMember.person.birthDate.date.content.substring(
                0,
                4
            ).toInt() else null,
            nationality = if (teamMember.person != null && teamMember.person.nationality != null) teamMember.person.nationality.country.alpha3.value else null,
            gender = if (teamMember.person != null) personConverter.convertGender(teamMember.person.sex) else null,
            punchingUnit = if (teamMember.cCard != null && teamMember.cCard.isNotEmpty()) competitorConverter.convertCCard(
                teamMember.cCard[0]
            ) else null,
            leg = teamMember.teamSequence.content.toInt(),
            entryFeeIds = if (teamMember.entryEntryFee != null) convertEntryFees(
                teamMember.entryEntryFee,
                raceId
            ) else listOf()
        )
    }
}
