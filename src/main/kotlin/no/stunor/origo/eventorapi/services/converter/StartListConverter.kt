package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.entry.Entry
import no.stunor.origo.eventorapi.model.event.entry.EntryStatus
import no.stunor.origo.eventorapi.model.event.entry.PersonEntry
import no.stunor.origo.eventorapi.model.event.entry.TeamEntry
import no.stunor.origo.eventorapi.model.event.entry.TeamMember
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class StartListConverter {
    @Autowired
    private lateinit var timeStampConverter: TimeStampConverter

    @Autowired
    private lateinit var personConverter: PersonConverter

    @Autowired
    private lateinit var organisationConverter: OrganisationConverter

    fun convertEventStartList(eventor: Eventor, startList: org.iof.eventor.StartList): List<Entry> {
        val entries = mutableListOf<Entry>()

        for (classStart in startList.classStart) {
            for (personOrTeamStart in classStart.personStartOrTeamStart) {
                when (personOrTeamStart) {
                    is org.iof.eventor.PersonStart -> convertPersonStart(eventor, startList.event, classStart, personOrTeamStart, entries)
                    is org.iof.eventor.TeamStart -> entries.add(convertTeamStart(eventor, startList.event, classStart, personOrTeamStart))
                }
            }
        }
        return entries
    }
    private fun convertPersonStart(
        eventor: Eventor,
        event: org.iof.eventor.Event,
        classStart: org.iof.eventor.ClassStart,
        personStart: org.iof.eventor.PersonStart,
        entries: MutableList<Entry>
    ) {
        if (event.eventRace.size > 1) {
            for (raceStart in personStart.raceStart) {
                entries.add(convertMultiDayPersonStart(eventor, classStart, personStart, raceStart))
            }
        } else {
            entries.add(convertOneDayPersonStart(eventor, event, classStart, personStart))
        }
    }

    private fun convertMultiDayPersonStart(
        eventor: Eventor,
        classStart: org.iof.eventor.ClassStart,
        personStart: org.iof.eventor.PersonStart,
        raceStart: org.iof.eventor.RaceStart
    ): Entry {
        return PersonEntry(
            raceId = raceStart.eventRaceId.content,
            classId = classStart.eventClass.eventClassId.content,
            personId = if (personStart.person.personId != null)
                personStart.person.personId.content
            else null,
            name = personConverter.convertPersonName(personStart.person.personName),
            organisation = if (personStart.organisation != null)
                organisationConverter.convertOrganisation(personStart.organisation, eventor)
            else organisationConverter.convertOrganisation(personStart.organisationId, eventor),
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
                timeStampConverter.parseDate(
                    "${raceStart.start.startTime.date.content} ${raceStart.start.startTime.clock.content}",
                    eventor)
            else null,
            finishTime = null,
            result = null,
            splitTimes = listOf(),
            status = EntryStatus.SignedUp
        )
    }

    private fun convertOneDayPersonStart(
        eventor: Eventor,
        event: org.iof.eventor.Event,
        classStart: org.iof.eventor.ClassStart,
        personStart: org.iof.eventor.PersonStart
    ): Entry {
        return PersonEntry(
            raceId = event.eventRace[0].eventRaceId.content,
            classId = classStart.eventClass.eventClassId.content,
            personId = if (personStart.person.personId != null) personStart.person.personId.content else null,
            name = personConverter.convertPersonName(personStart.person.personName),
            organisation = if (personStart.organisation != null)
                organisationConverter.convertOrganisation(personStart.organisation, eventor)
            else organisationConverter.convertOrganisation(personStart.organisationId, eventor),
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
                timeStampConverter.parseDate(
                    "${personStart.start.startTime.date.content} ${personStart.start.startTime.clock.content}",
                    eventor)
            else null,
            status = EntryStatus.SignedUp
        )
    }

    private fun convertTeamStart(
        eventor: Eventor,
        event: org.iof.eventor.Event,
        classStart: org.iof.eventor.ClassStart,
        teamStart: org.iof.eventor.TeamStart
    ): Entry {
        return TeamEntry(
            raceId = event.eventRace[0].eventRaceId.content,
            classId = classStart.eventClass.eventClassId.content,
            name = teamStart.teamName.content,
            organisations = organisationConverter.convertOrganisations(
                organisations = teamStart.organisationIdOrOrganisationOrCountryId,
                eventor = eventor
            ),
            teamMembers = convertTeamMembers(eventor, teamStart.teamMemberStart),
            bib = if (teamStart.bibNumber != null) teamStart.bibNumber.content else null,
            startTime = if (teamStart.startTime != null) timeStampConverter.parseDate(
                "${teamStart.startTime.date.content} ${teamStart.startTime.clock.content}",
                eventor
            ) else null,
            status = EntryStatus.SignedUp
        )

    }

    private fun convertTeamMembers(
        eventor: Eventor,
        teamMembers: List<org.iof.eventor.TeamMemberStart>
    ): List<TeamMember> {
        val result  = mutableListOf<TeamMember>()
        for (teamMember in teamMembers) {
            result.add(convertTeamMember(eventor, teamMember))
        }
        return result
    }

    private fun convertTeamMember(eventor: Eventor, teamMember: org.iof.eventor.TeamMemberStart): TeamMember {
        return TeamMember(
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
            startTime = if (teamMember.startTime != null) timeStampConverter.parseDate(
                "${teamMember.startTime.date.content} ${teamMember.startTime.clock.content}",
                eventor
            ) else null,
        )
    }

    companion object {
        private const val YEAR_SUBSTRING_START = 0
        private const val YEAR_SUBSTRING_END = 4
    }
}
