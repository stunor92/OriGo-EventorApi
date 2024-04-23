package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.event.EventClass
import java.io.Serializable

data class CalendarPersonResult(
        var result: Result,
        var bib: String? = null,
        var eventClass: EventClass = EventClass()
): Serializable
