package no.stunor.origo.eventorapi.model.event

import java.sql.Timestamp

data class EntryBreak(
        val from: Timestamp? = null,
        val to: Timestamp? = null
)
