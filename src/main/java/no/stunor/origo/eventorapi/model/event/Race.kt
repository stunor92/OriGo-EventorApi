package no.stunor.origo.eventorapi.model.event

import com.google.cloud.Timestamp
import java.io.Serializable

data class Race (
        var raceId: String = "",
        var name: String = "",
        var lightCondition: LightConditionEnum = LightConditionEnum.DAY,
        var distance: DistanceEnum = DistanceEnum.MIDDLE,
        var date: Timestamp? = null,
        var position: Position? = null,
        var startList: Boolean = false,
        var resultList: Boolean = false,
        var livelox: Boolean = false,
) : Serializable
