package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.event.EventClass
import java.io.Serializable

data class CalendarTeamResult(
        var teamName: String = "",
        var leg: Int = 0,
        var legTime: Int? = null,
        var time: Int? = null,
        var timeBehind: Int? =  null,
        val position: Int? = null,
        val status: String = "OK",
        var bib: String? = null,
        var eventClass: EventClass = EventClass()
) : Serializable
