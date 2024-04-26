package no.stunor.origo.eventorapi.model.event

import com.google.cloud.Timestamp
import java.io.Serializable

data class EntryBreak(
        val from: Timestamp? = null,
        val to: Timestamp? = null
) : Serializable
