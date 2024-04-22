package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.DocumentId
import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.origo.entry.EntryBreak
import java.io.Serializable
import java.util.*

data class Event (
        @DocumentId
        @JsonIgnore
        var id: String? = null,
        var eventorId: String? = null,
        var eventId: String = "",
        var name: String = "",
        var type: EventFormEnum = EventFormEnum.INDIVIDUAL,
        var classification: EventClassificationEnum = EventClassificationEnum.CLUB,
        var status: EventStatusEnum = EventStatusEnum.CREATED,
        var disciplines: List<DisciplineEnum> = ArrayList(),
        var startDate: Timestamp? = null,
        var finishDate: Timestamp? = null,
        var organisers: List<Organisation> = ArrayList(),
        var regions: List<Region> = ArrayList(),
        var eventClasses: List<EventClass> = ArrayList(),
        var documents: List<Document> = ArrayList(),
        var entryBreaks: List<EntryBreak> = ArrayList(),
        var races: List<Race> = ArrayList(),
        var punchingUnitTypes: List<String> = ArrayList(),
        var webUrls: List<String>? = ArrayList(),
        var message: String? = null,
        var email: String? = null,
        var phone: String? = null
) : Serializable
