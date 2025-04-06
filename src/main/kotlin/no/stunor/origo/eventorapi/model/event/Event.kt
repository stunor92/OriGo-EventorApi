package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime

data class Event (
        @JsonIgnore
        var id: String? = null,
        var eventorId: String? = null,
        var eventId: String = "",
        var name: String = "",
        var type: EventFormEnum = EventFormEnum.Individual,
        var classification: EventClassificationEnum = EventClassificationEnum.Club,
        var status: EventStatusEnum = EventStatusEnum.Created,
        var disciplines: List<DisciplineEnum> = ArrayList(),
        var startDate: ZonedDateTime? = null,
        var finishDate: ZonedDateTime? = null,
        var organisers: List<String> = ArrayList(),
        var regions: List<String> = ArrayList(),
        var eventClasses: List<EventClass> = ArrayList(),
        var documents: List<EventorDocument> = ArrayList(),
        var entryBreaks: List<EntryBreak> = ArrayList(),
        var races: List<Race> = ArrayList(),
        var punchingUnitTypes: List<PunchingUnitType> = ArrayList(),
        var webUrls: List<String>? = ArrayList(),
        var message: String? = null,
        var email: String? = null,
        var phone: String? = null
)
