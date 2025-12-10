package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import java.sql.Timestamp
import java.util.UUID


data class Race(
    var id: UUID? = null,
    var eventorRef: String = "",
    var name: String = "",
    var lightCondition: LightConditionEnum = LightConditionEnum.Day,
    var distance: DistanceEnum = DistanceEnum.Middle,
    var date: Timestamp? = null,
    var position: RacePosition? = null,
    var startList: Boolean = false,
    var resultList: Boolean = false,
    var livelox: Boolean = false,
    @JsonIgnore var event: Event = Event()
)