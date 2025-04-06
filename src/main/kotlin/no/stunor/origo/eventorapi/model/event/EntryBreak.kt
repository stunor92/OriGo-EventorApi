package no.stunor.origo.eventorapi.model.event

import java.time.ZonedDateTime

data class EntryBreak(
        val from: ZonedDateTime? = null,
        val to: ZonedDateTime? = null
)
