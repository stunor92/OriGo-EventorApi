package no.stunor.origo.eventorapi.model.event

import io.hypersistence.utils.hibernate.type.array.EnumArrayType
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import io.hypersistence.utils.hibernate.type.array.TimestampArrayType
import io.hypersistence.utils.hibernate.type.array.internal.AbstractArrayType
import jakarta.persistence.*
import no.stunor.origo.eventorapi.model.organisation.Organisation
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
    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "event_organiser",
        joinColumns = [JoinColumn(name = "event_id", referencedColumnName = "eventId"), JoinColumn(name = "eventor_id", referencedColumnName = "eventorId")],
        inverseJoinColumns = [JoinColumn(name = "organisation_id", referencedColumnName = "organisationId")]
    )
    var organisers: List<Organisation> = ArrayList(),
    @Column(name = "organisers")@Type(ListArrayType::class) var organisationId: List<String> = ArrayList(),

    @Type(ListArrayType::class) var regions: List<String> = ArrayList(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var classes: List<EventClass> = ArrayList(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var documents: List<Document> = ArrayList(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var fees: List<Fee> = ArrayList(),
    @Type(TimestampArrayType::class) var entryBreaks: Array<Timestamp> = arrayOf(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var races: List<Race> = ArrayList(),
    @Type(value = ListArrayType::class) var webUrls: List<String> = listOf(),
    var message: String? = null,
    var email: String? = null,
    var phone: String? = null
){
    override fun toString(): String {
        return "$eventorId: $eventId - $name"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Event

        if (eventorId != other.eventorId) return false
        if (eventId != other.eventId) return false
        if (name != other.name) return false
        if (type != other.type) return false
        if (classification != other.classification) return false
        if (status != other.status) return false
        if (!disciplines.contentEquals(other.disciplines)) return false
        if (!punchingUnitTypes.contentEquals(other.punchingUnitTypes)) return false
        if (startDate != other.startDate) return false
        if (finishDate != other.finishDate) return false
        if (organisers != other.organisers) return false
        if (regions != other.regions) return false
        if (classes != other.classes) return false
        if (documents != other.documents) return false
        if (fees != other.fees) return false
        if (!entryBreaks.contentEquals(other.entryBreaks)) return false
        if (races != other.races) return false
        if (webUrls != other.webUrls) return false
        if (message != other.message) return false
        if (email != other.email) return false
        if (phone != other.phone) return false

        return true
    }

    override fun hashCode(): Int {
        var result = eventorId.hashCode()
        result = 31 * result + eventId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + classification.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + disciplines.contentHashCode()
        result = 31 * result + punchingUnitTypes.contentHashCode()
        result = 31 * result + (startDate?.hashCode() ?: 0)
        result = 31 * result + (finishDate?.hashCode() ?: 0)
        result = 31 * result + organisers.hashCode()
        result = 31 * result + regions.hashCode()
        result = 31 * result + classes.hashCode()
        result = 31 * result + documents.hashCode()
        result = 31 * result + fees.hashCode()
        result = 31 * result + entryBreaks.contentHashCode()
        result = 31 * result + races.hashCode()
        result = 31 * result + webUrls.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        return result
    }
}