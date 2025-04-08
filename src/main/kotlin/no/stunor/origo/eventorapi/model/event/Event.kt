package no.stunor.origo.eventorapi.model.event

import io.hypersistence.utils.hibernate.type.array.EnumArrayType
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import io.hypersistence.utils.hibernate.type.array.TimestampArrayType
import io.hypersistence.utils.hibernate.type.array.internal.AbstractArrayType
import jakarta.persistence.*
import org.hibernate.annotations.Type
import java.io.Serializable
import java.sql.Timestamp

data class EventId(
    private val eventId: String,
    private val eventorId: String
) : Serializable {
    constructor() : this("", "")
}


@Entity
@IdClass(EventId::class)
data class Event(
    @Id var eventorId: String = "",
    @Id var eventId: String = "",
    var name: String = "",
    @Enumerated(EnumType.STRING) var type: EventFormEnum = EventFormEnum.Individual,
    @Enumerated(EnumType.STRING) var classification: EventClassificationEnum = EventClassificationEnum.Club,
    @Enumerated(EnumType.STRING) var status: EventStatusEnum = EventStatusEnum.Created,
    @Type(
        value = EnumArrayType::class,
        parameters = [org.hibernate.annotations.Parameter(

            name = AbstractArrayType.SQL_ARRAY_TYPE,
            value = "discipline"
        )]
    )
    @Column(
        name = "disciplines",
        columnDefinition = "discipline[]"
    )
    var disciplines: Array<Discipline> = arrayOf(),
    @Type(
        value = EnumArrayType::class,
        parameters = [org.hibernate.annotations.Parameter(
            name = AbstractArrayType.SQL_ARRAY_TYPE,
            value = "punching_unit_type"
        )]
    )
    @Column(
        name = "punching_unit_types",
        columnDefinition = "punching_unit_type[]"
    )
    var punchingUnitTypes: Array<PunchingUnitType> = arrayOf(),
    var startDate: Timestamp? = null,
    var finishDate: Timestamp? = null,
    @Type(ListArrayType::class) var organisers: List<String> = ArrayList(),
    @Type(ListArrayType::class) var regions: List<String> = ArrayList(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var eventClasses: List<EventClass> = ArrayList(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var documents: List<Document> = ArrayList(),
    @Type(TimestampArrayType::class) var entryBreaks: Array<Timestamp> = arrayOf(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var races: List<Race> = ArrayList(),
    @Type(value = ListArrayType::class) var webUrls: List<String> = listOf(),
    var message: String? = null,
    var email: String? = null,
    var phone: String? = null
)