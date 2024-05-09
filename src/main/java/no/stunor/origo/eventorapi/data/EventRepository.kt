package no.stunor.origo.eventorapi.data

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import no.stunor.origo.eventorapi.model.event.Event
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface EventRepository : FirestoreReactiveRepository<Event> {
    fun findByEventIdAndEventorId(eventId: String, eventorId: String): Mono<Event>
}