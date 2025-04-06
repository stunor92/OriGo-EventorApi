package no.stunor.origo.eventorapi.model.event.competitor

import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName
import java.time.ZonedDateTime


data class TeamMemberCompetitor(
        var personId: String? = null,
        var name: PersonName? = null,
        var birthYear: Int? = null,
        var nationality: String? = null,
        var gender: Gender? = null,
        var punchingUnits: List<PunchingUnit> = listOf(),
        var leg: Int = 1,
        var startTime: ZonedDateTime? = null,
        var finishTime: ZonedDateTime? = null,
        var legResult: Result? = null,
        var overallResult: Result? = null,
        var splitTimes: List<SplitTime> = listOf(),
        var entryFeeIds: List<String> = listOf()
)
