package no.stunor.origo.eventorapi.data

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import no.stunor.origo.eventorapi.model.event.competitor.PersonCompetitor
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface PersonCompetitorRepository : FirestoreReactiveRepository<PersonCompetitor> {
    fun findAllByEventIdAndEventorId(eventId: String, eventorId: String): Flux<PersonCompetitor>
}