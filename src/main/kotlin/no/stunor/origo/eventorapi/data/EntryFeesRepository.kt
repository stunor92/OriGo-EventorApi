package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.EntryFee
import no.stunor.origo.eventorapi.model.event.Event
import org.springframework.stereotype.Repository


@Repository
open class EntryFeesRepository {
    //private val firestore = FirestoreClient.getFirestore()

    fun saveAll(eventDocument: String, entryFees: List<EntryFee>) {
        /*for (entryFee in entryFees) {
            if(entryFee.id == null) {
                firestore
                    .collection("events")
                    .document(eventDocument)
                    .collection("fees")
                    .add(entryFee)
            } else {
                firestore
                    .collection("events")
                    .document(eventDocument)
                    .collection("fees")
                    .document(entryFee.id!!)
                    .set(entryFee)
            }
        }*/
    }

    fun delete(eventDocument: String, fee: EntryFee) {
        /*fee.id?.let {
            firestore.collection("events")
                .document(eventDocument)
                .collection("fees")
                .document(it)
                .delete()
        }*/
    }

    fun findAllForEvent(event: Event): List<EntryFee> {
        /*val future: ApiFuture<QuerySnapshot> = firestore.collection("events")
            .document(event.id?:"")
            .collection("fees")
            .get()

        val documents = future.get().documents

        val result: MutableList<EntryFee> = mutableListOf()

        for (document in documents) {
            result.add(document.toObject(EntryFee::class.java))
        }
        return result.toList()*/
        return listOf()
    }
}