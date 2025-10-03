package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.entry.Entry
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@Repository
interface EntryRepository : CrudRepository<Entry, String> {
    fun findAllByEventIdAndEventorId(eventId: String, eventorId: String): List<Entry>
}