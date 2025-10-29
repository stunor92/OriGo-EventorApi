package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.UUID

@Entity
@Table(name = "class")
data class EventClass(
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    var id: UUID? = null,
    var eventorRef: String = "",
    var name: String = "",
    var shortName: String = "",
    @Enumerated(EnumType.STRING) var type: EventClassTypeEnum = EventClassTypeEnum.Normal,
    var minAge: Int? = 0,
    var maxAge: Int? = 99,
    @Enumerated(EnumType.STRING) var gender: ClassGender = ClassGender.Both,
    var presentTime: Boolean = true,
    var orderedResult: Boolean = true,
    var legs: Int = 1,
    var minAverageAge: Int? = 0,
    var maxAverageAge: Int? = 99,
    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @JsonIgnore var event: Event? = null
)