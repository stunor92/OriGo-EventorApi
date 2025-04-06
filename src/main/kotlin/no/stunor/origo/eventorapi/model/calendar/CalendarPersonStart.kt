package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.event.EventClass
import java.time.ZonedDateTime

data class CalendarPersonStart(
        var startTime: ZonedDateTime? = null,
        var bib: String? = null,
        var eventClass: EventClass = EventClass()
)
