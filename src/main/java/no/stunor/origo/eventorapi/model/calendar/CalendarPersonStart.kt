package no.stunor.origo.eventorapi.model.calendar

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.event.EventClass
import java.io.Serializable

data class CalendarPersonStart(
        var startTime: Timestamp? = null,
        var bib: String? = null,
        var eventClass: EventClass = EventClass()
) : Serializable
