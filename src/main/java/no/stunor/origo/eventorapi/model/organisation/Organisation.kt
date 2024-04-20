package no.stunor.origo.eventorapi.model.organisation

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.spring.data.firestore.Document
import java.io.Serializable

@Document(collectionName = "organisations")
data class Organisation (
    var organisationId: String = "",
    var eventorId: String = "",
    var name: String = "",
    var type: OrganisationType = OrganisationType.CLUB,
    var country: String = "",
    @JsonIgnore
    var email: String = "",
    @JsonIgnore
    var apiKey: String? = null,
    @JsonIgnore
    var regionId: String? = null,
    @JsonIgnore
    var contactPerson: String? = null
) : Serializable
