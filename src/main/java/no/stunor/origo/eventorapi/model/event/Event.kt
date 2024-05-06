package no.stunor.origo.eventorapi.model.event

import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.DocumentId
import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.organisation.Organisation
import java.io.Serializable
import java.util.*

data class Event (
        @DocumentId
        var id: String = "",
        var eventorId: String? = null,
        var eventId: String = "",
        var name: String = "",
        var type: EventFormEnum = EventFormEnum.Individual,
        var classification: EventClassificationEnum = EventClassificationEnum.Club,
        var status: EventStatusEnum = EventStatusEnum.Created,
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
