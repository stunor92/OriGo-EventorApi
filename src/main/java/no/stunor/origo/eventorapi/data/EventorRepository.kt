package no.stunor.origo.eventorapi.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.QuerySnapshot
import com.google.firebase.cloud.FirestoreClient
import no.stunor.origo.eventorapi.model.Eventor
import org.springframework.stereotype.Repository


@Repository
class EventorRepository{
    private val firestore = FirestoreClient.getFirestore()

    fun findAll(): List<Eventor> {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("eventors").get()
        val documents = future.get().documents

        val result: MutableList<Eventor> = mutableListOf()

        for (document in documents) {
            result.add(document.toObject(Eventor::class.java))
        }
        return result.toList()
    }

    fun findByEventorId(eventorId: String): Eventor? {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("eventors")
            .whereEqualTo("eventorId", eventorId)
            .get()

        return if(future.get().isEmpty){
            null
        } else {
            future.get().documents.first().toObject(Eventor::class.java)
        }
    }

}