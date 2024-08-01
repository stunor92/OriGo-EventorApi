package no.stunor.origo.eventorapi.data

import com.google.firebase.cloud.FirestoreClient
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import org.springframework.stereotype.Service


@Service
class CompetitorRepository {
    private val firestore = FirestoreClient.getFirestore()

    fun saveAll(event: Event, competitors: List<Competitor>) {
        if(event.id == null) return
        for (competitor in competitors) {
            if(competitor.id == null) {
                firestore.collection("events").document(event.id!!).collection("competitors").add(competitor)
            } else {
                firestore.collection("events").document(event.id!!).collection("competitors").document(competitor.id!!).set(competitor)
            }
        }
    }

}