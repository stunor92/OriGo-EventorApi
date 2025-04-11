package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.Serializable
import java.sql.Timestamp

data class RaceId(
        private val eventorId: String,
        private val eventId: String,
        private val raceId: String,
) : Serializable {
        constructor() : this("", "", "")
}

@Entity
@IdClass(RaceId::class)
data class Race (
        @JsonIgnore @Id var eventorId: String = "",
        @JsonIgnore @Id var eventId: String = "",
        @Id var raceId: String = "",
        var name: String = "",
        @Enumerated(EnumType.STRING) var lightCondition: LightConditionEnum = LightConditionEnum.Day,
        @Enumerated(EnumType.STRING) var distance: DistanceEnum = DistanceEnum.Middle,
        var date: Timestamp? = null,
        @Embedded var position:RacePosition?  = null,
        var startList: Boolean = false,
        var resultList: Boolean = false,
        var livelox: Boolean = false,
        @ManyToOne
        @JoinColumns(
                JoinColumn(name = "eventId", referencedColumnName = "eventId", insertable = false, updatable = false),
                JoinColumn(name = "eventorId", referencedColumnName = "eventorId", insertable = false, updatable = false)
        )
        @JsonIgnore var event: Event? = null,
)