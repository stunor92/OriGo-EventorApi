package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.event.EventClass
import java.time.Instant

data class CalendarTeamStart(
        var teamName: String = "",
        var startTime: Instant? = null,
        var bib: String? = null,
        var leg: Int = 0,
        var eventClass: EventClass = EventClass()
)
