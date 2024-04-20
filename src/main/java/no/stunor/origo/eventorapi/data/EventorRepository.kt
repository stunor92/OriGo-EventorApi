package no.stunor.origo.eventorapi.data

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import no.stunor.origo.eventorapi.model.Eventor
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface EventorRepository : FirestoreReactiveRepository<Eventor?> {
    fun findByEventorId(eventorId: String?): Mono<Eventor?>?
}