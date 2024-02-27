package no.stunor.origo.eventorapi.repository;

import org.springframework.stereotype.Repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import no.stunor.origo.eventorapi.model.firestore.Region;
import reactor.core.publisher.Flux;

@Repository
public interface RegionRepository extends FirestoreReactiveRepository<Region> {
    Flux<Region> findByOrganisationIdAndEventor(String organisationId, String eventor);
}