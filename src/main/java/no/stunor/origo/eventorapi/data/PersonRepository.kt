package no.stunor.origo.eventorapi.data

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import no.stunor.origo.eventorapi.model.person.Person
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PersonRepository : FirestoreReactiveRepository<Person> {
    fun findAllByUsersContains(user: String): Flux<Person>
    fun findAllByUsersContainsAndEventorId(user: String, eventorId: String): Flux<Person>
    fun findByPersonIdAndEventorId(personId: String, eventorId: String): Mono<Person>
}