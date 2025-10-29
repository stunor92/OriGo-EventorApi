package no.stunor.origo.eventorapi.model.organisation

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import no.stunor.origo.eventorapi.model.Region
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.util.*

@Entity
data class Organisation (
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    var id: UUID? = null,
    @JsonIgnore var eventorId: String = "",
    var eventorRef: String = "",
    var name: String = "",
    @Enumerated(EnumType.STRING) var type: OrganisationType = OrganisationType.Club,
    var country: String = "",
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    var region: Region? = null
)

enum class OrganisationType {
    Club, Region, Federation, IOF
}
