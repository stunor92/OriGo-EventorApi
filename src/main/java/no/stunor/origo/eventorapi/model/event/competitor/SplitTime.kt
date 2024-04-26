package no.stunor.origo.eventorapi.model.event.competitor

import java.io.Serializable

data class SplitTime(
        val sequence: Int,
        val controlCode: String,
        val time: Int
) : Serializable
