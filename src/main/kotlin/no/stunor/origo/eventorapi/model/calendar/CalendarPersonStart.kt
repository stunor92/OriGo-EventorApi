package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.event.EventClass
import java.time.Instant

data class CalendarPersonStart(
        var startTime: Instant? = null,
        var bib: String? = null,
        var eventClass: EventClass = EventClass()
)
