package no.stunor.origo.eventorapi.model.event

import com.google.cloud.Timestamp
import com.google.cloud.firestore.GeoPoint


data class Race (
        var raceId: String = "",
        var name: String = "",
        var lightCondition: LightConditionEnum = LightConditionEnum.Day,
        var distance: DistanceEnum = DistanceEnum.Middle,
        var date: Timestamp? = null,
        var position:GeoPoint?  = null,
        var startList: Boolean = false,
        var resultList: Boolean = false,
        var livelox: Boolean = false,
)
