package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrganisationRepository : CrudRepository<Organisation, String> {
    fun findByOrganisationIdAndEventorId(organisationId: String, eventorId: String): Organisation?
}