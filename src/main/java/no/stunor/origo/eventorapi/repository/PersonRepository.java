package no.stunor.origo.eventorapi.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;

import no.stunor.origo.eventorapi.model.firestore.Person;

@Repository
public interface PersonRepository extends FirestoreReactiveRepository<Person> {
    List<Person> findByUsers(String user);
    List<Person> findByUsersAndEventor(String user, String eventor);
    Person findByPersonIdAndEventor(String personId, String eventor);

}