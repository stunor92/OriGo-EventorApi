package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.sql.Timestamp
import java.util.UUID


@Entity
data class Race(
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    var id: UUID? = null,
    var eventorRef: String = "",
    var name: String = "",
    @Enumerated(EnumType.STRING) var lightCondition: LightConditionEnum = LightConditionEnum.Day,
    @Enumerated(EnumType.STRING) var distance: DistanceEnum = DistanceEnum.Middle,
    var date: Timestamp? = null,
    @Embedded var position: RacePosition? = null,
    var startList: Boolean = false,
    var resultList: Boolean = false,
    var livelox: Boolean = false,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnore var event: Event = Event()
)