package no.stunor.origo.eventorapi.model.calendar

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.event.EventClass
import java.io.Serializable

data class CalendarPersonResult(
        var time: Int? = null,
        var timeBehind: Int? =  null,
        val position: Int? = null,
        val status: String = "OK",
        var bib: String? = null,
        var eventClass: EventClass = EventClass()
): Serializable
