package no.stunor.origo.eventorapi.data

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import org.springframework.stereotype.Repository

@Repository
interface CompetitorRepository : FirestoreReactiveRepository<Competitor> {
}