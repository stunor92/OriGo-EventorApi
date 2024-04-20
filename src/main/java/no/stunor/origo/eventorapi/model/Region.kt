package no.stunor.origo.eventorapi.model

import com.google.cloud.spring.data.firestore.Document
import java.io.Serializable

@Document(collectionName = "regions")
data class Region (
        var eventorId: String = "",
        var regionId: String = "",
        var name: String = "",
) : Serializable