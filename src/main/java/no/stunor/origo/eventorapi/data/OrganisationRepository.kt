package no.stunor.origo.eventorapi.data

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface OrganisationRepository : FirestoreReactiveRepository<Organisation> {
    fun findByOrganisationIdAndEventorId(organisationId: String, eventorId: String): Mono<Organisation>
}