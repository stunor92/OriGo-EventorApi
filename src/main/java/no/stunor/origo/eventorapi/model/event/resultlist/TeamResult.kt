package no.stunor.origo.eventorapi.model.event.resultlist

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.event.competitor.Result
import no.stunor.origo.eventorapi.model.organisation.Organisation

data class TeamResult(
        override var raceId: String = "",
        override var eventClassId: String = "",
        var organisations: List<Organisation> = listOf(),
        var teamMembers: List<TeamMemberResult> = listOf(),
        override var name: Any = "",
        override var bib: String? = null,
        override var startTime: Timestamp? = null,
        override var finishTime: Timestamp? = null,
        var result: Result? = null,
) : CompetitorResult