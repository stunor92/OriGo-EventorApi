package no.stunor.origo.eventorapi.model.event.competitor

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.organisation.Organisation

data class TeamCompetitor(
        override var eventorId: String = "",
        override var eventId: String = "",
        override var raceId: String = "",
        override var eventClassId: String = "",
        var organisations: List<Organisation> = listOf(),
        var teamMembers: List<TeamMemberCompetitor> = listOf(),
        var name: String = "",
        override var bib: String? = null,
        override var startTime: Timestamp? = null,
        override var finishTime: Timestamp? = null,
        var result: Result? = null,
        override var entryFeeIds: List<String> = listOf()
) : Competitor
