package no.stunor.origo.eventorapi.model.event

import java.io.Serializable

data class Price (
    var amount: Int = 0,
    var currency: String = ""
) : Serializable
