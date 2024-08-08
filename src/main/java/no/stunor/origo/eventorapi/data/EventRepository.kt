package no.stunor.origo.eventorapi.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.QuerySnapshot
import com.google.firebase.cloud.FirestoreClient
import no.stunor.origo.eventorapi.model.event.Event
import org.springframework.stereotype.Repository


@Repository
class EventRepository {
    private val firestore = FirestoreClient.getFirestore()

    fun findByEventIdAndEventorId(eventId: String, eventorId: String): Event? {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("events")
            .whereEqualTo("eventId", eventId)
            .whereEqualTo("eventorId", eventorId)
            .get()

        return if(future.get().isEmpty){
            null
        } else {
            future.get().documents.first().toObject(Event::class.java)
        }
    }

    fun save(event: Event) {
        if(event.id == null) {
            firestore.collection("events").add(event)
        } else {
            firestore.collection("events").document(event.id!!).set(event)
        }
    }
}