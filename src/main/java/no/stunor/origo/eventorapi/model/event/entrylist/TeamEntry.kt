package no.stunor.origo.eventorapi.model.event.entrylist

import no.stunor.origo.eventorapi.model.organisation.Organisation

data class TeamEntry(
        override var raceId: String = "",
        override var eventClassId: String = "",
        var organisations: List<Organisation> = listOf(),
        var teamMembers: List<TeamMemberEntry> = listOf(),
        override var name: Any = "",
) : CompetitorEntry
