package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.Fee
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface FeeRepository : CrudRepository<Fee, UUID> {
    fun findAllByEventId(eventId: UUID?): List<Fee>
}