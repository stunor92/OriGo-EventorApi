package no.stunor.origo.eventorapi.model.event

import java.io.Serializable


data class EventClass (
        var eventClassId: String = "",
        var name: String = "",
        var shortName: String = "",
        var type: EventClassTypeEnum = EventClassTypeEnum.Normal,
        var minAge: Int? = 0,
        var maxAge: Int? = 99,
        var gender: String = "B",
        var presentTime: Boolean = true,
        var orderedResult: Boolean = true,
        var legs: Int = 1,
        var minAverageAge: Int? = 0,
        var maxAverageAge: Int? = 99,
        var entryFees: List<String> = listOf()
) : Serializable