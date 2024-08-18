package no.stunor.origo.eventorapi.model.event.startlist

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.organisation.Organisation

data class TeamStart(
        override var raceId: String = "",
        override var eventClassId: String = "",
        var organisations: List<Organisation> = listOf(),
        var teamMembers: List<TeamMemberStart> = listOf(),
        override var name: Any = "",
        override var bib: String? = null,
        override var startTime: Timestamp? = null,
) : CompetitorStart