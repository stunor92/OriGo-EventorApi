package no.stunor.origo.eventorapi.model.event.competitor.eventor

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.event.competitor.Result
import no.stunor.origo.eventorapi.model.organisation.Organisation

data class EventorTeamCompetitor(
        override var raceId: String = "",
        override var eventClassId: String = "",
        var organisations: List<Organisation> = listOf(),
        var teamMembers: List<EventorTeamMemberCompetitor> = listOf(),
        override var name: Any = "",
        override var bib: String? = null,
        override var startTime: Timestamp? = null,
        override var finishTime: Timestamp? = null,
        var result: Result? = null,
) : EventorCompetitor