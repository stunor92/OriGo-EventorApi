package no.stunor.origo.eventorapi.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.QuerySnapshot
import com.google.firebase.cloud.FirestoreClient
import no.stunor.origo.eventorapi.model.event.competitor.Competitor
import org.springframework.stereotype.Repository


@Repository
class CompetitorRepository {
    private val firestore = FirestoreClient.getFirestore()

    fun saveAll(documentId: String, competitors: List<Competitor>) {
        for (competitor in competitors) {
            if(competitor.id == null) {
                firestore.collection("events").document(documentId).collection("competitors").add(competitor)
            } else {
                firestore.collection("events").document(documentId).collection("competitors").document(competitor.id!!).set(competitor)
            }
        }
    }

    fun findAllByEventIdAndEventorId(eventId: String, eventorId: String): List<Competitor> {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("events")
            .document(eventId)
            .collection("competitors")
            .whereEqualTo("eventId", eventId)
            .whereEqualTo("eventorId", eventorId)
            .get()

        val documents = future.get().documents

        val result: MutableList<Competitor> = mutableListOf()

        for (document in documents) {
            result.add(document.toObject(Competitor::class.java))
        }
        return result.toList()
    }
}