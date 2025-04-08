package no.stunor.origo.eventorapi.model.calendar

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.stunor.origo.eventorapi.config.TimestampISO8601Serializer
import no.stunor.origo.eventorapi.model.event.EventClass
import java.sql.Timestamp

data class CalendarPersonStart(
        @JsonSerialize(using = TimestampISO8601Serializer::class) var startTime: Timestamp? = null,
        var bib: String? = null,
        var eventClass: EventClass = EventClass()
)
