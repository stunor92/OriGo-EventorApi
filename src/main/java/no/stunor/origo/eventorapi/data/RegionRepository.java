package no.stunor.origo.eventorapi.data;

import org.springframework.stereotype.Repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import no.stunor.origo.eventorapi.model.firestore.Region;
import reactor.core.publisher.Mono;

@Repository
public interface RegionRepository extends FirestoreReactiveRepository<Region> {
    Mono<Region> findByOrganisationIdAndEventorId(String organisationId, String eventorId);

}