package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.firestore.annotation.DocumentId
import com.google.cloud.firestore.annotation.Exclude
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.origo.entry.EntryBreak
import no.stunor.origo.eventorapi.model.origo.event.*
import java.io.Serializable
import java.time.Instant
import java.util.*

data class Event (
        @DocumentId
        @JsonIgnore
        var id: String? = null,
        @Exclude
        var eventor: Eventor? = null,
        @JsonIgnore
        var eventorId: String? = null,
        var eventId: String = "",
        var name: String = "",
        var type: EventFormEnum = EventFormEnum.INDIVIDUAL,
        var classification: EventClassificationEnum = EventClassificationEnum.CLUB,
        var status: EventStatusEnum = EventStatusEnum.CREATED,
        var disciplines: List<DisciplineEnum> = ArrayList(),
        var startDate: Date = Date.from(Instant.now()),
        var finishDate: Date = Date.from(Instant.now()),
        var organisers: List<Organisation> = ArrayList(),
        var regions: List<Region> = ArrayList(),
        var eventClasses: List<EventClass> = ArrayList(),
        var documents: List<Document> = ArrayList(),
        var entryBreaks: List<EntryBreak> = ArrayList(),
        @Exclude
        var races: List<Race> = ArrayList(),
        var punchingUnitTypes: List<String> = ArrayList(),
        var webUrls: List<String>? = ArrayList(),
        var message: String? = null,
        var email: String? = null,
        var phone: String? = null
) : Serializable
