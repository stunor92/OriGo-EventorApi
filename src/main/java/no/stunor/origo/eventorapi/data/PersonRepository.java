package no.stunor.origo.eventorapi.data;

import org.springframework.stereotype.Repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import no.stunor.origo.eventorapi.model.firestore.Person;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface PersonRepository extends FirestoreReactiveRepository<Person> {
    Flux<Person> findAllByUsersContains(String user);
    Flux<Person> findAllByUsersContainsAndEventorId(String user, String eventorId);
    Mono<Person> findByPersonIdAndEventorId(String personId, String eventorId); 

}