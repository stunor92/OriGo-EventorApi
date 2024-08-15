package no.stunor.origo.eventorapi.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.QuerySnapshot
import com.google.firebase.cloud.FirestoreClient
import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.springframework.stereotype.Repository

@Repository
class OrganisationRepository {
    private val firestore = FirestoreClient.getFirestore()

    fun findByOrganisationIdAndEventorId(organisationId: String, eventorId: String): Organisation? {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("organisations")
            .whereEqualTo("organisationId", organisationId)
            .whereEqualTo("eventorId", eventorId)
            .get()

        return if(future.get().isEmpty){
            null
        } else {
            future.get().documents.first().toObject(Organisation::class.java)
        }
    }

}