package no.stunor.origo.eventorapi.model.event

import java.io.Serializable

data class EventorDocument (
        var name: String = "",
        var url: String = "",
        var type: String = ""
) : Serializable
