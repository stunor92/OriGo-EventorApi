package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.event.EventClass
import java.io.Serializable

data class CalendarEntry(
        var eventClass: EventClass? = null,
        var punchingUnits: List<PunchingUnit> = listOf(),
        var punchingUnit: PunchingUnit? = null
) : Serializable
