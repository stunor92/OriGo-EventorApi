package no.stunor.origo.eventorapi.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.QuerySnapshot
import com.google.firebase.cloud.FirestoreClient
import no.stunor.origo.eventorapi.model.event.EntryFee
import org.springframework.stereotype.Repository


@Repository
class EntryFeesRepository {
    private val firestore = FirestoreClient.getFirestore()

    fun saveAll(documentId: String, entryFees: List<EntryFee>) {
        for (entryFee in entryFees) {
            if(entryFee.id == null) {
                firestore.collection("events").document(documentId).collection("fees").add(entryFee)
            } else {
                firestore.collection("events").document(documentId).collection("fees").document(entryFee.id!!).set(entryFee)
            }
        }
    }

    fun findAllByEventIdAndEventorId(eventId: String, eventorId: String): List<EntryFee> {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("events")
            .document(eventId)
            .collection("fees")
            .whereEqualTo("eventId", eventId)
            .whereEqualTo("eventorId", eventorId)
            .get()

        val documents = future.get().documents

        val result: MutableList<EntryFee> = mutableListOf()

        for (document in documents) {
            result.add(document.toObject(EntryFee::class.java))
        }
        return result.toList()
    }
}