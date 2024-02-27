package no.stunor.origo.eventorapi.repository;

import org.springframework.stereotype.Repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import no.stunor.origo.eventorapi.model.firestore.Organisation;
import reactor.core.publisher.Flux;

@Repository
public interface OrganisationRepository extends FirestoreReactiveRepository<Organisation> {
    Flux<Organisation> findByOrganisationIdAndEventor(String organisationId, String eventor);

}