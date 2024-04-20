package no.stunor.origo.eventorapi.model.event

import java.io.Serializable

data class Position (
        var x: Double = 0.0,
        var y: Double = 0.0
) : Serializable