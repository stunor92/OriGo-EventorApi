package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.EventClass
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ClassRepository : CrudRepository<EventClass, String> {
    fun findAllByEventIdAndEventorId(eventId: String, eventorId: String): List<EventClass>
}