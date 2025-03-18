package no.stunor.origo.eventorapi.model.event.competitor

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName


data class TeamMemberCompetitor(
        var personId: String? = null,
        var name: PersonName? = null,
        var birthYear: Int? = null,
        var nationality: String? = null,
        var gender: Gender? = null,
        var punchingUnits: List<PunchingUnit> = listOf(),
        var leg: Int = 1,
        var startTime: Timestamp? = null,
        var finishTime: Timestamp? = null,
        var legResult: Result? = null,
        var overallResult: Result? = null,
        var splitTimes: List<SplitTime> = listOf(),
        var entryFeeIds: List<String> = listOf()
)
