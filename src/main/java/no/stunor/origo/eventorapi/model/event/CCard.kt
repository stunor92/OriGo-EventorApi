package no.stunor.origo.eventorapi.model.event

import java.io.Serializable

data class CCard (
        var id: String = "",
        var type: String = ""
) : Serializable