package no.stunor.origo.eventorapi.model.organisation

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import no.stunor.origo.eventorapi.model.Region
import java.io.Serializable

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
        @JsonIgnore @Id var eventorId: String = "",
        var name: String = "",
        @Enumerated(EnumType.STRING) var type: OrganisationType = OrganisationType.Club,
        var country: String = "",
        @ManyToOne
        @JoinColumns(
                JoinColumn(name = "eventorId", referencedColumnName = "eventorId", insertable = false, updatable = false),
                JoinColumn(name = "regionId", referencedColumnName = "regionId", insertable = false, updatable = false)
        )
        var region: Region? = null,
)

enum class OrganisationType {
        Club, Region, Federation, IOF
}
