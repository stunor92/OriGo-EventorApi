package no.stunor.origo.eventorapi.model.organisation

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.firestore.annotation.DocumentId
import java.io.Serializable

data class Organisation (
        @JsonIgnore
        @DocumentId
        var id: String? = null,
        var organisationId: String? = null,
        var eventorId: String = "",
        var name: String = "",
        var type: OrganisationType = OrganisationType.Club,
        var country: String? = null,
        @JsonIgnore
        var email: String? = null,
        @JsonIgnore
        var apiKey: String? = null,
        @JsonIgnore
        var regionId: String? = null,
        @JsonIgnore
        var contactPerson: String? = null
) : Serializable
