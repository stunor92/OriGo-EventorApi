package no.stunor.origo.eventorapi.repository;

import org.springframework.stereotype.Repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import no.stunor.origo.eventorapi.model.firestore.Eventor;
import reactor.core.publisher.Flux;

@Repository
public interface EventorRepository extends FirestoreReactiveRepository<Eventor> {
    Flux<Eventor> findByEventorId(String eventorId);
}