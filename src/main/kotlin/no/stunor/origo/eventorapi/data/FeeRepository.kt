package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.Fee
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface FeeRepository : CrudRepository<Fee, String> {
    fun findAllByEventIdAndEventorId(eventId: String, eventorId: String): List<Fee>
}