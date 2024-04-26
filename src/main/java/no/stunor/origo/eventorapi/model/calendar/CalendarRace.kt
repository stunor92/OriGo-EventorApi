package no.stunor.origo.eventorapi.model.calendar

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.event.*
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.event.Position
import no.stunor.origo.eventorapi.model.event.EntryBreak
import java.io.Serializable

data class CalendarRace(
        var eventorId: String = "",
        var eventId: String = "",
        var eventName: String = "",
        var raceId: String = "",
        var raceName: String? = null,
        var raceDate: Timestamp? = null,
        var type: EventFormEnum = EventFormEnum.INDIVIDUAL,
        var classification: EventClassificationEnum = EventClassificationEnum.CLUB,
        var lightCondition: LightConditionEnum = LightConditionEnum.DAY,
        var distance: DistanceEnum = DistanceEnum.MIDDLE,
        var position: Position? = null,
        var status: EventStatusEnum = EventStatusEnum.APPLIED,
        var disciplines: List<DisciplineEnum> = listOf(),
        var organisers: List<Organisation> = listOf(),
        var entryBreaks: List<EntryBreak> = listOf(),
        var entries: Int = 0,
        var userEntries: MutableList<CalendarCompetitor> = mutableListOf(),
        var organisationEntries: MutableMap<String, Int> = mutableMapOf(),
        var startList: Boolean = false,
        var resultList: Boolean = false,
        var livelox: Boolean = false
) : Serializable