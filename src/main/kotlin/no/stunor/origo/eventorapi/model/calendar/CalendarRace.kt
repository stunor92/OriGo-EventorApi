package no.stunor.origo.eventorapi.model.calendar

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.stunor.origo.eventorapi.config.TimestampISO8601Serializer
import no.stunor.origo.eventorapi.config.TimestampListISO8601Serializer
import no.stunor.origo.eventorapi.model.event.*
import java.sql.Timestamp

data class CalendarRace(
    var eventorId: String = "",
    var eventId: String = "",
    var eventName: String = "",
    var raceId: String = "",
    var raceName: String? = null,
    @JsonSerialize(using = TimestampISO8601Serializer::class) var raceDate: Timestamp,
    var type: EventFormEnum = EventFormEnum.Individual,
    var classification: EventClassificationEnum = EventClassificationEnum.Club,
    var lightCondition: LightConditionEnum = LightConditionEnum.Day,
    var distance: DistanceEnum = DistanceEnum.Middle,
    var position: RacePosition? = null,
    var status: EventStatusEnum = EventStatusEnum.Applied,
    var disciplines: List<Discipline> = listOf(),
    var organisers: List<String> = listOf(),
    @JsonSerialize(using = TimestampListISO8601Serializer::class) var entryBreaks: List<Timestamp> = listOf(),
    var entries: Int = 0,
    var userEntries: MutableList<CalendarCompetitor> = mutableListOf(),
    var organisationEntries: MutableMap<String, Int> = mutableMapOf(),
    var signedUp: Boolean = false,
    var startList: Boolean = false,
    var resultList: Boolean = false,
    var livelox: Boolean = false
)