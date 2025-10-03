package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.event.EventClass
import no.stunor.origo.eventorapi.model.event.entry.Result

data class CalendarPersonResult(
        var result: Result,
        var bib: String? = null,
        var eventClass: EventClass = EventClass()
)
