package no.stunor.origo.eventorapi.data;

import org.springframework.stereotype.Repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import no.stunor.origo.eventorapi.model.organisation.Organisation;
import reactor.core.publisher.Mono;

@Repository
public interface OrganisationRepository extends FirestoreReactiveRepository<Organisation> {
    Mono<Organisation> findByOrganisationIdAndEventorId(String organisationId, String eventorId);
}