package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import io.hypersistence.utils.hibernate.type.array.EnumArrayType
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import io.hypersistence.utils.hibernate.type.array.TimestampArrayType
import io.hypersistence.utils.hibernate.type.array.internal.AbstractArrayType
import jakarta.persistence.*
import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.Type
import org.hibernate.type.SqlTypes
import java.sql.Timestamp
import java.util.*

@Entity
data class Event(
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    var id: UUID? = null,
    @JsonIgnore var eventorId: String = "",
    var eventorRef: String = "",
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
    @Column(name = "disciplines", columnDefinition = "discipline[]")
    var disciplines: Array<Discipline> = emptyArray(),
    @Type(
        value = EnumArrayType::class,
        parameters = [org.hibernate.annotations.Parameter(
            name = AbstractArrayType.SQL_ARRAY_TYPE,
            value = "punching_unit_type"
        )]
    )
    @Column(name = "punching_unit_types", columnDefinition = "punching_unit_type[]")
    var punchingUnitTypes: Array<PunchingUnitType> = emptyArray(),
    var startDate: Timestamp? = null,
    var finishDate: Timestamp? = null,
    @ManyToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "event_organiser",
        joinColumns = [JoinColumn(name = "event_id")],
        inverseJoinColumns = [JoinColumn(name = "organisation_id")]
    )
    var organisers: MutableList<Organisation> = mutableListOf(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var classes: MutableList<EventClass> = mutableListOf(),
    @OneToMany(cascade = [CascadeType.PERSIST, CascadeType.MERGE], mappedBy = "event", orphanRemoval = true) var documents: MutableList<Document> = mutableListOf(),
    @Type(TimestampArrayType::class) var entryBreaks: Array<Timestamp> = emptyArray(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var races: MutableList<Race> = mutableListOf(),
    @Type(value = ListArrayType::class) var webUrls: List<String> = listOf(),
    var message: String? = null,
    var email: String? = null,
    var phone: String? = null
) {
    private fun basicFieldsEqual(other: Event): Boolean {
        val checks = listOf(
            { id == other.id },
            { eventorId == other.eventorId },
            { eventorRef == other.eventorRef },
            { name == other.name },
            { type == other.type },
            { classification == other.classification },
            { status == other.status },
            { startDate == other.startDate },
            { finishDate == other.finishDate },
            { message == other.message },
            { email == other.email },
            { phone == other.phone }
        )
        return checks.all { it() }
    }

    private fun arraysEqual(other: Event): Boolean {
        return disciplines.contentEquals(other.disciplines) &&
                punchingUnitTypes.contentEquals(other.punchingUnitTypes) &&
                entryBreaks.contentEquals(other.entryBreaks)
    }

    private fun collectionsEqual(other: Event): Boolean {
        return organisers == other.organisers &&
                classes == other.classes &&
                documents == other.documents &&
                races == other.races &&
                webUrls == other.webUrls
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Event
        if (!basicFieldsEqual(other)) return false
        if (!arraysEqual(other)) return false
        if (!collectionsEqual(other)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + eventorId.hashCode()
        result = 31 * result + eventorRef.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + classification.hashCode()
        result = 31 * result + status.hashCode()
        result = 31 * result + disciplines.contentHashCode()
        result = 31 * result + punchingUnitTypes.contentHashCode()
        result = 31 * result + (startDate?.hashCode() ?: 0)
        result = 31 * result + (finishDate?.hashCode() ?: 0)
        result = 31 * result + organisers.hashCode()
        result = 31 * result + classes.hashCode()
        result = 31 * result + documents.hashCode()
        result = 31 * result + entryBreaks.contentHashCode()
        result = 31 * result + races.hashCode()
        result = 31 * result + webUrls.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        return result
    }
}