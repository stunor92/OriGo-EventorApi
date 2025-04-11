package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.Serializable

data class EventClassId(
        private val classId: String,
        private val eventId: String,
        private val eventorId: String
) : Serializable {
        constructor() : this("", "", "")
}
@Entity
@IdClass(EventClassId::class)
@Table(name = "class")
data class EventClass (
        @JsonIgnore @Id var eventorId: String = "",
        @JsonIgnore @Id var eventId: String = "",
        @Id var classId: String = "",
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
        @JoinColumns(
                JoinColumn(name = "eventId", referencedColumnName = "eventId", insertable = false, updatable = false),
                JoinColumn(name = "eventorId", referencedColumnName = "eventorId", insertable = false, updatable = false)
        )
        @JsonIgnore var event: Event? = null
)