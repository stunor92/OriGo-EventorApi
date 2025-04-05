package no.stunor.origo.eventorapi.model.calendar


import no.stunor.origo.eventorapi.model.event.DisciplineEnum
import no.stunor.origo.eventorapi.model.event.DistanceEnum
import no.stunor.origo.eventorapi.model.event.EntryBreak
import no.stunor.origo.eventorapi.model.event.EventClassificationEnum
import no.stunor.origo.eventorapi.model.event.EventFormEnum
import no.stunor.origo.eventorapi.model.event.EventStatusEnum
import no.stunor.origo.eventorapi.model.event.LightConditionEnum
import java.sql.Timestamp

data class CalendarRace(
    var eventorId: String = "",
    var eventId: String = "",
    var eventName: String = "",
    var raceId: String = "",
    var raceName: String? = null,
    var raceDate: Timestamp? = null,
    var type: EventFormEnum = EventFormEnum.Individual,
    var classification: EventClassificationEnum = EventClassificationEnum.Club,
    var lightCondition: LightConditionEnum = LightConditionEnum.Day,
    var distance: DistanceEnum = DistanceEnum.Middle,
    //var position: GeoPoint? = null,
    var status: EventStatusEnum = EventStatusEnum.Applied,
    var disciplines: List<DisciplineEnum> = listOf(),
    var organisers: List<String> = listOf(),
    var entryBreaks: List<EntryBreak> = listOf(),
    var entries: Int = 0,
    var userEntries: MutableList<CalendarCompetitor> = mutableListOf(),
    var organisationEntries: MutableMap<String, Int> = mutableMapOf(),
    var signedUp: Boolean = false,
    var startList: Boolean = false,
    var resultList: Boolean = false,
    var livelox: Boolean = false
)