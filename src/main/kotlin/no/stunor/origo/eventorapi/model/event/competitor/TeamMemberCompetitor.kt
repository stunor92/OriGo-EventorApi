package no.stunor.origo.eventorapi.model.event.competitor

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.stunor.origo.eventorapi.config.TimestampISO8601Serializer
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName
import java.sql.Timestamp

data class TeamMemberCompetitor(
        var personId: String? = null,
        var name: PersonName? = null,
        var birthYear: Int? = null,
        var nationality: String? = null,
        var gender: Gender? = null,
        var punchingUnits: List<PunchingUnit> = listOf(),
        var leg: Int = 1,
        @JsonSerialize(using = TimestampISO8601Serializer::class) var startTime: Timestamp? = null,
        @JsonSerialize(using = TimestampISO8601Serializer::class) var finishTime: Timestamp? = null,
        var legResult: Result? = null,
        var overallResult: Result? = null,
        var splitTimes: List<SplitTime> = listOf(),
        var entryFeeIds: List<String> = listOf()
)
