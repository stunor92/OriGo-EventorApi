package no.stunor.origo.eventorapi.model.event.competitor

import java.io.Serializable

data class SplitTime(
        val sequence: Int = 0,
        val controlCode: String = "",
        val time: Int? = null
) : Serializable
