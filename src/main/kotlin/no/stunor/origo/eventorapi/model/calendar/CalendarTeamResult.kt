package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.event.EventClass
import no.stunor.origo.eventorapi.model.event.entry.Result

data class CalendarTeamResult(
        var teamName: String = "",
        var leg: Int = 0,
        var result: Result,
        var legResult: Result,
        var bib: String? = null,
        var eventClass: EventClass = EventClass()
)
