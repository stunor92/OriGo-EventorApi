package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.event.EventClass
import java.sql.Timestamp

data class CalendarPersonStart(
        var startTime: Timestamp? = null,
        var bib: String? = null,
        var eventClass: EventClass = EventClass()
)
