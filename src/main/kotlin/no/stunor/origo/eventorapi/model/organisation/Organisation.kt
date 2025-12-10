package no.stunor.origo.eventorapi.model.organisation

import com.fasterxml.jackson.annotation.JsonIgnore
import no.stunor.origo.eventorapi.model.Region
import java.util.*

data class Organisation (
    var id: UUID? = null,
    @JsonIgnore var eventorId: String = "",
    var eventorRef: String = "",
    var name: String = "",
    var type: OrganisationType = OrganisationType.Club,
    var country: String = "",
    var region: Region? = null
)

enum class OrganisationType {
    Club, Region, Federation, IOF
}
