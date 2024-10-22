package no.stunor.origo.eventorapi.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.QuerySnapshot
import com.google.firebase.cloud.FirestoreClient
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.event.Event
import no.stunor.origo.eventorapi.model.event.EventClass
import org.springframework.stereotype.Repository


@Repository
class EventClassRepository{
    private val firestore = FirestoreClient.getFirestore()

    fun findAllByEventorIdAndEventId(event: Event): List<EventClass> {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("events")
            .document(event.id!!)
            .collection("classes")
            .get()

        val result: MutableList<EventClass> = mutableListOf()

        for (document in future.get().documents) {
            result.add(document.toObject(EventClass::class.java))
        }
        return result.toList()

    }

    fun save(event: Event, eventClass: EventClass) {
        if(eventClass.id == null) {
            firestore.collection("events")
                .document(event.id!!)
                .collection("classes")
                .add(eventClass)
        } else {
            firestore.collection("events")
                .document(event.id!!)
                .collection("classes")
                .document(eventClass.id!!)
                .set(eventClass)
        }
    }

}