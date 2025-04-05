package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.Event
import org.springframework.stereotype.Repository

@Repository
open class EventRepository {
   // private val firestore = FirestoreClient.getFirestore()

    fun findByEventIdAndEventorId(eventId: String, eventorId: String): Event? {
        /*al future: ApiFuture<QuerySnapshot> = firestore.collection("events")
            .whereEqualTo("eventId", eventId)
            .whereEqualTo("eventorId", eventorId)
            .get()

        return if(future.get().isEmpty){
            null
        } else {
            future.get().documents.first().toObject(Event::class.java)
        }*/

        return null
    }

    fun save(event: Event) {
        /*if(event.id == null) {
            firestore.collection("events").add(event)
        } else {
            firestore.collection("events").document(event.id!!).set(event)
        }

         */
    }
}