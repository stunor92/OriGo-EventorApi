package no.stunor.origo.eventorapi.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import no.stunor.origo.eventorapi.model.firestore.Person;
import reactor.core.publisher.Flux;

@Repository
public interface PersonRepository extends FirestoreReactiveRepository<Person> {
    Flux<Person> findByUsers(String user);
    Flux<Person> findByUsersAndEventor(String user, String eventor);
    Flux<Person> findByPersonIdAndEventor(String personId, String eventor);

}