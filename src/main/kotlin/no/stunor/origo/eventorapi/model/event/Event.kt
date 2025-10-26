package no.stunor.origo.eventorapi.model.event


import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
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
    @Id
    @JsonIgnore
    val eventorId: String = "",
    @ManyToOne
    @JoinColumns(
        JoinColumn(name = "eventorId", referencedColumnName = "eventorId", insertable = false, updatable = false)
    )
    var eventor: Eventor = Eventor(),
    @Id var eventId: String = "",
    var name: String = "",
    @Enumerated(EnumType.STRING) var type: EventFormEnum = EventFormEnum.Individual,
    @Enumerated(EnumType.STRING) var classification: EventClassificationEnum = EventClassificationEnum.Club,
    @Enumerated(EnumType.STRING) var status: EventStatusEnum = EventStatusEnum.Created,
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "disciplines", columnDefinition = "discipline[]")
    var disciplines: Array<Discipline> = arrayOf(),
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "punching_unit_types", columnDefinition = "punching_unit_type[]")
    var punchingUnitTypes: Array<PunchingUnitType> = arrayOf(),
    var startDate: Timestamp? = null,
    var finishDate: Timestamp? = null,
    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "event_organiser",
        joinColumns = [JoinColumn(name = "event_id", referencedColumnName = "eventId"), JoinColumn(
            name = "eventor_id",
            referencedColumnName = "eventorId"
        )],
        inverseJoinColumns = [JoinColumn(name = "organisation_id", referencedColumnName = "organisationId")]
    )
    var organisers: MutableList<Organisation> = ArrayList(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var classes: MutableList<EventClass> = ArrayList(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var documents: MutableList<Document> = ArrayList(),
    @JdbcTypeCode(SqlTypes.ARRAY) @Column(
        name = "entry_breaks",
        columnDefinition = "timestamp[]"
    ) var entryBreaks: List<Timestamp> = listOf(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "event") var races: MutableList<Race> = ArrayList(),
    @JdbcTypeCode(SqlTypes.ARRAY) @Column(
        name = "web_urls",
        columnDefinition = "text[]"
    ) var webUrls: List<String> = listOf(),
    var message: String? = null,
    var email: String? = null,
    var phone: String? = null
) {
    override fun toString(): String {
        return "Event(eventId='$eventId', eventorId='$eventorId', name='$name')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Event) return false

        return eventorId == other.eventorId &&
                eventId == other.eventId &&
                name == other.name &&
                type == other.type &&
                classification == other.classification &&
                status == other.status &&
                disciplines.contentEquals(other.disciplines) &&
                punchingUnitTypes.contentEquals(other.punchingUnitTypes) &&
                startDate == other.startDate &&
                finishDate == other.finishDate &&
                organisers == other.organisers &&
                classes == other.classes &&
                documents == other.documents &&
                entryBreaks == other.entryBreaks &&
                races == other.races &&
                webUrls == other.webUrls &&
                message == other.message &&
                email == other.email &&
                phone == other.phone
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
        result = 31 * result + classes.hashCode()
        result = 31 * result + documents.hashCode()
        result = 31 * result + entryBreaks.hashCode()
        result = 31 * result + races.hashCode()
        result = 31 * result + webUrls.hashCode()
        result = 31 * result + (message?.hashCode() ?: 0)
        result = 31 * result + (email?.hashCode() ?: 0)
        result = 31 * result + (phone?.hashCode() ?: 0)
        return result
    }
}