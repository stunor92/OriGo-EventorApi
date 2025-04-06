package no.stunor.origo.eventorapi.model.organisation

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.io.Serializable
import java.sql.Timestamp
import java.time.Instant

data class OrganisationId(
        private val organisationId: String,
        private val eventorId: String
) : Serializable {
        constructor() : this("", "")
}

@Entity
@IdClass(OrganisationId::class)
data class Organisation (
        @Id var organisationId: String = "",
        @Id var eventorId: String = "",
        var name: String = "",
        @Enumerated(EnumType.STRING) var type: OrganisationType = OrganisationType.Club,
        var country: String = "",
        var email: String? = null,
        @JsonIgnore var eventorApiKey: String? = null,
        var regionId: String? = null,
        var contactPerson: String? = null,
        @JsonIgnore var lastUpdated: Timestamp = Timestamp.from(Instant.now())
)

enum class OrganisationType {
        Club, Region, Federation, IOF
}
