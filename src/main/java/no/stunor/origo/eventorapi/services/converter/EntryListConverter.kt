package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.origo.entry.EventEntryList
import no.stunor.origo.eventorapi.model.origo.entry.PersonEntry
import no.stunor.origo.eventorapi.model.origo.entry.TeamEntry
import no.stunor.origo.eventorapi.model.origo.entry.TeamMemberEntry
import org.iof.eventor.Entry
import org.iof.eventor.EntryEntryFee
import org.iof.eventor.EntryList
import org.iof.eventor.TeamCompetitor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EntryListConverter {
    @Autowired
    private lateinit var personConverter: PersonConverter

    @Autowired
    private lateinit var eventConverter: EventConverter

    @Autowired
    private lateinit var eventClassConverter: EventClassConverter

    @Autowired
    private lateinit var organisationRepository: OrganisationRepository
    fun convertEventEntryList(entryList: EntryList, eventor: Eventor): EventEntryList {
        val personEntries: MutableList<PersonEntry> = ArrayList()
        val teamEntries: MutableList<TeamEntry> = ArrayList()

        for (entry in entryList.entry) {
            if (entry.competitor != null) {
                personEntries.add(convertPersonEntry(entry, eventor))
            } else if (entry.teamCompetitor != null) {
                teamEntries.add(convertTeamEntry(entry, eventor))
            }
        }
        return EventEntryList(personEntries, teamEntries)
    }

    private fun convertPersonEntry(entry: Entry, eventor: Eventor): PersonEntry {
        return PersonEntry(
                entry.entryId.content,
                personConverter.convertCompetitor(entry.competitor.person, eventor),
                if (entry.competitor.organisation != null && entry.competitor.organisation.organisationId != null) organisationRepository.findByOrganisationIdAndEventorId(entry.competitor.organisation.organisationId.content, eventor.eventorId).block() else null,
                if (entry.competitor.cCard != null && entry.competitor.cCard.isNotEmpty()) eventConverter.convertCCard(entry.competitor.cCard[0]) else null,
                if (entry.bibNumber != null) entry.bibNumber.content else "",
                if (entry.eventRaceId != null) eventConverter.convertEventRaceIds(entry.eventRaceId) else ArrayList(),
                if (entry.entryEntryFee != null) convertEntryFees(entry.entryEntryFee) else ArrayList(),
                if (entry.entryClass != null && entry.entryClass.isNotEmpty()) eventClassConverter.convertEventClassId(entry.entryClass[0]) else null
        )
    }


    private fun convertTeamEntry(entry: Entry, eventor: Eventor): TeamEntry {
        return TeamEntry(
                entry.entryId.content,
                convertTeamOrganisations(entry.teamCompetitor, eventor),
                convertTeamMembers(entry.teamCompetitor, eventor),
                entry.teamName.content,
                if (entry.bibNumber != null) entry.bibNumber.content else "",
                if (entry.entryEntryFee != null) convertEntryFees(entry.entryEntryFee) else ArrayList(),
                if (entry.entryClass != null) eventClassConverter.convertEventClassIds(entry.entryClass) else ArrayList()
        )
    }

    private fun convertTeamOrganisations(teamCompetitors: List<TeamCompetitor>, eventor: Eventor): List<Organisation> {
        val result: MutableList<Organisation> = ArrayList()
        for (teamCompetitor in teamCompetitors) {
            if (teamCompetitor.organisation != null) {
                var organisationExist = false
                for ((organisationId) in result) {
                    if (organisationId == teamCompetitor.organisation.organisationId.content) {
                        organisationExist = true
                    }
                }
                if (!organisationExist) {
                    organisationRepository.findByOrganisationIdAndEventorId(teamCompetitor.organisation.organisationId.content, eventor.eventorId).block()?.let { result.add(it) }
                }
            }
        }
        return result
    }

    private fun convertTeamMembers(teamMembers: List<TeamCompetitor>, eventor: Eventor): List<TeamMemberEntry> {
        val result: MutableList<TeamMemberEntry> = ArrayList()
        for (teamMember in teamMembers) {
            result.add(convertTeamMember(teamMember, eventor))
        }
        return result
    }

    private fun convertEntryFees(entryFees: List<EntryEntryFee>): List<String> {
        val result: MutableList<String> = ArrayList()
        for (entryFee in entryFees) {
            result.add(entryFee.entryFeeId.content)
        }
        return result
    }

    private fun convertTeamMember(teamMember: TeamCompetitor, eventor: Eventor): TeamMemberEntry {
        return TeamMemberEntry(
                if (teamMember.person != null) personConverter.convertCompetitor(teamMember.person, eventor) else null,
                teamMember.teamSequence.content.toInt(),
                if (teamMember.cCard != null && teamMember.cCard.isNotEmpty()) eventConverter.convertCCard(teamMember.cCard[0]) else null
        )
    }
}
