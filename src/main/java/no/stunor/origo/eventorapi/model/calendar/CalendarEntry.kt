package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.event.CCard
import no.stunor.origo.eventorapi.model.event.EventClass
import java.io.Serializable

data class CalendarEntry(
        var eventClass: EventClass? = null,
        var cCard: CCard? = null
) : Serializable
