package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.event.PunchingUnitType
import no.stunor.origo.eventorapi.model.event.entry.Entry
import no.stunor.origo.eventorapi.model.event.entry.EntryStatus
import no.stunor.origo.eventorapi.model.event.entry.PersonEntry
import no.stunor.origo.eventorapi.model.event.entry.TeamEntry
import no.stunor.origo.eventorapi.model.event.entry.TeamMember
import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.iof.eventor.EntryList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EntryListConverter {
    @Autowired
    private lateinit var organisationConverter: OrganisationConverter

    fun convertEventEntryList(entryList: EntryList, eventor: Eventor): List<Entry> {
        val result  = mutableListOf<Entry>()

        for (entry in entryList.entry) {
            if (entry.competitor != null) {
                result.addAll(convertPersonEventEntries(entry, eventor))
            } else if (entry.teamCompetitor != null) {
                result.addAll(convertTeamEventEntries(entry, eventor))
            }
        }
        return result
    }

    private fun convertPersonEventEntries(entry: org.iof.eventor.Entry, eventor: Eventor): List<Entry> {
        val result  = mutableListOf<Entry>()

        for (raceId in entry.eventRaceId) {
            result.add(
                PersonEntry(
                    raceId = raceId.content,
                    classId = entry.entryClass[0].eventClassId.content,
                    personId = if (entry.competitor.person.personId != null) entry.competitor.person.personId.content else null,
                    name = PersonConverter.convertPersonName(entry.competitor.person.personName),
                    organisation =
                        if(entry.competitor.organisation != null)
                            organisationConverter.convertOrganisation(entry.competitor.organisation, eventor)
                        else
                            organisationConverter.convertOrganisation(entry.competitor.organisationId, eventor),
                    birthYear = if (entry.competitor.person.birthDate != null) entry.competitor.person.birthDate.date.content.substring(
                        0,
                        4
                    ).toInt() else null,
                    nationality = if (entry.competitor.person.nationality?.country != null) entry.competitor.person.nationality.country.alpha3.value else null,
                    gender = PersonConverter.convertGender(entry.competitor.person.sex),
                    punchingUnits = convertPunchingUnits(entry.competitor.cCard),
                    bib = if (entry.bibNumber != null) entry.bibNumber.content else null,
                    startTime = null,
                    finishTime = null,
                    result = null,
                    splitTimes = mutableListOf(),
                    status = EntryStatus.SignedUp
                )
            )
        }
        return result
    }


    private fun convertTeamEventEntries(entry: org.iof.eventor.Entry, eventor: Eventor): List<Entry> {
        val result  = mutableListOf<Entry>()

        for (race in entry.teamCompetitor[0].entryEntryFee) {
            result.add(
                TeamEntry(
                    raceId = race.eventRaceId,
                    classId = entry.entryClass[0].eventClassId.content,
                    name = entry.teamName.content,
                    organisations =  convertTeamOrganisations(entry.teamCompetitor, eventor),
                    bib = if (entry.bibNumber != null) entry.bibNumber.content else null,
                    teamMembers = convertTeamMembers(entry.teamCompetitor, race.eventRaceId),
                    startTime = null,
                    finishTime = null,
                    result = null,
                    status = EntryStatus.SignedUp
                )
            )

        }
        return result
    }

    private fun convertTeamOrganisations(
        teamCompetitors: List<org.iof.eventor.TeamCompetitor>,
        eventor: Eventor
    ): MutableList<Organisation> {
        val result  = mutableListOf<Organisation>()
        for (teamCompetitor in teamCompetitors) {
            if (teamCompetitor.organisationId != null
                && !result.any { it.organisationId == teamCompetitor.organisationId.content }
            ) {
                organisationConverter.convertOrganisation(
                    organisation = teamCompetitor.organisationId,
                    eventor = eventor
                )?.let {
                    result.add(
                        it
                    )
                }

            }
        }
        return result
    }

    private fun convertTeamMembers(
        teamMembers: List<org.iof.eventor.TeamCompetitor>,
        raceId: String
    ): MutableList<TeamMember> {
        val result  = mutableListOf<TeamMember>()
        for (teamMember in teamMembers) {
            result.add(convertTeamMember(teamMember))
        }
        return result
    }


    private fun convertTeamMember(teamMember: org.iof.eventor.TeamCompetitor): TeamMember {
        return TeamMember(
            personId = if (teamMember.person != null && teamMember.person.personId != null) teamMember.person.personId.content else null,
            name = if (teamMember.person != null) PersonConverter.convertPersonName(teamMember.person.personName) else null,
            birthYear = if (teamMember.person != null && teamMember.person.birthDate != null) teamMember.person.birthDate.date.content.substring(
                0,
                4
            ).toInt() else null,
            nationality = if (teamMember.person != null && teamMember.person.nationality != null) teamMember.person.nationality.country.alpha3.value else null,
            gender = if (teamMember.person != null) PersonConverter.convertGender(teamMember.person.sex) else null,
            punchingUnits = convertPunchingUnits(teamMember.cCard),
            leg = teamMember.teamSequence.content.toInt(),
            startTime = null,
            finishTime = null,
            legResult = null,
            overallResult = null,
            splitTimes = mutableListOf(),
        )
    }

    fun convertPunchingUnits(cCards: List<org.iof.eventor.CCard>): MutableList<PunchingUnit> {
        val punchingUnits: MutableList<PunchingUnit> = ArrayList()
        for (c in cCards) {
            punchingUnits.add(convertPunchingUnit(c))
        }
        return punchingUnits
    }

    private fun convertPunchingUnit(cCard: org.iof.eventor.CCard): PunchingUnit {
        return PunchingUnit(cCard.cCardId.content, convertPunchingUnitType(cCard.punchingUnitType.value))
    }

    fun convertPunchingUnitTypes(punchingUnitTypes: List<org.iof.eventor.PunchingUnitType>): List<PunchingUnitType> {
        val result: MutableList<PunchingUnitType> = ArrayList()
        for (punchingUnitType in punchingUnitTypes) {
            result.add(convertPunchingUnitType(punchingUnitType.value))
        }
        return result
    }

    private fun convertPunchingUnitType(value: String): PunchingUnitType {
        return when (value) {
            "manual" -> PunchingUnitType.Manual
            "Emit" -> PunchingUnitType.Emit
            "SI" -> PunchingUnitType.SI
            "emiTag" -> PunchingUnitType.EmiTag
            else -> PunchingUnitType.Other
        }
    }
}
