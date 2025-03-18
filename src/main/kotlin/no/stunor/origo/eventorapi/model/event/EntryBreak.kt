package no.stunor.origo.eventorapi.model.event

import com.google.cloud.Timestamp


data class EntryBreak(
        val from: Timestamp? = null,
        val to: Timestamp? = null
)
