package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface OrganisationRepository : CrudRepository<Organisation, UUID> {
    fun findByEventorRefAndEventorId(eventorRef: String, eventorId: String): Organisation?
}