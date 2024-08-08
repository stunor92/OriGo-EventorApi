package no.stunor.origo.eventorapi.model

import java.io.Serializable

data class Region (
        var eventorId: String = "",
        var regionId: String = "",
        var name: String = "",
) : Serializable