package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.Event
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface EventRepository : CrudRepository<Event, String> {
    fun findByEventIdAndEventorId(eventId: String, eventorId: String): Event?
}