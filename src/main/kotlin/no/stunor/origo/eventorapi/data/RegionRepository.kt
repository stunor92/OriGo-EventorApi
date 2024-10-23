package no.stunor.origo.eventorapi.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.QuerySnapshot
import com.google.firebase.cloud.FirestoreClient
import no.stunor.origo.eventorapi.model.Region
import org.springframework.stereotype.Repository

@Repository
class RegionRepository {
    private val firestore = FirestoreClient.getFirestore()

    fun findByRegionIdAndEventorId(regionId: String, eventorId: String): Region? {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("regions")
            .whereEqualTo("regionId", regionId)
            .whereEqualTo("eventorId", eventorId)
            .get()

        return if(future.get().isEmpty){
            null
        } else {
            future.get().documents.first().toObject(Region::class.java)
        }
    }
}