package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.DocumentId
import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.organisation.SimpleOrganisation
import java.io.Serializable
import java.util.*

data class Event (
        @JsonIgnore
        @DocumentId
        var id: String? = null,
        var eventorId: String? = null,
        var eventId: String = "",
        var name: String = "",
        var type: EventFormEnum = EventFormEnum.Individual,
        var classification: EventClassificationEnum = EventClassificationEnum.Club,
        var status: EventStatusEnum = EventStatusEnum.Created,
        var disciplines: List<DisciplineEnum> = ArrayList(),
        var startDate: Timestamp? = null,
        var finishDate: Timestamp? = null,
        var organisers: List<SimpleOrganisation> = ArrayList(),
        var regions: List<Region> = ArrayList(),
        var eventClasses: List<EventClass> = ArrayList(),
        var documents: List<EventorDocument> = ArrayList(),
        var entryBreaks: List<EntryBreak> = ArrayList(),
        var races: List<Race> = ArrayList(),
        var punchingUnitTypes: List<PunchingUnitType> = ArrayList(),
        var webUrls: List<String>? = ArrayList(),
        var message: String? = null,
        var email: String? = null,
        var phone: String? = null
) : Serializable
