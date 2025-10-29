package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.Event
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EventRepository : CrudRepository<Event, UUID> {
    fun findByEventorIdAndEventorRef(eventorId: String, eventorRef: String): Event?
}
