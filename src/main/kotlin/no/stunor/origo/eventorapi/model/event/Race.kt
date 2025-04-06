package no.stunor.origo.eventorapi.model.event

import java.time.ZonedDateTime


data class Race (
        var raceId: String = "",
        var name: String = "",
        var lightCondition: LightConditionEnum = LightConditionEnum.Day,
        var distance: DistanceEnum = DistanceEnum.Middle,
        var date: ZonedDateTime? = null,
        var position:RacePosition?  = null,
        var startList: Boolean = false,
        var resultList: Boolean = false,
        var livelox: Boolean = false,
)
