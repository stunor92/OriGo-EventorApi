package no.stunor.origo.eventorapi.model.event

import java.time.Instant

data class EntryBreak(
        val from: Instant? = null,
        val to: Instant? = null
)
