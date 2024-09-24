package no.stunor.origo.eventorapi.data

import com.google.api.core.ApiFuture
import com.google.cloud.firestore.QuerySnapshot
import com.google.firebase.cloud.FirestoreClient
import no.stunor.origo.eventorapi.model.organisation.FullOrganisation
import no.stunor.origo.eventorapi.model.organisation.SimpleOrganisation
import org.springframework.stereotype.Repository

@Repository
class OrganisationRepository {
    private val firestore = FirestoreClient.getFirestore()

    fun findByOrganisationIdAndEventorId(organisationId: String, eventorId: String): SimpleOrganisation? {
        val future: ApiFuture<QuerySnapshot> = firestore.collection("organisations")
            .whereEqualTo("organisationId", organisationId)
            .whereEqualTo("eventorId", eventorId)
            .get()

        return if(future.get().isEmpty){
            null
        } else {
            future.get().documents.first().toObject(SimpleOrganisation::class.java)
        }
    }

    fun save(organisation: FullOrganisation) {
        if(organisation.id == null) {
            firestore.collection("organisations").add(organisation)
        } else {
            firestore.collection("organisations").document(organisation.id!!).set(organisation)
        }
    }
}