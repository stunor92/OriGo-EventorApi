package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.sql.Timestamp
import java.util.UUID

@Entity
data class Fee (
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.UUID)
    var id: UUID? = null,
    var eventorRef: String = "",
    var name: String = "",
    var currency: String? = null,
    var amount: Double? = null,
    var externalFee: Double? = null,
    var percentageSurcharge: Int? = null,
    var validFrom: Timestamp? = null,
    var validTo: Timestamp? = null,
    var fromBirthYear: Int? = null,
    var toBirthYear: Int? = null,
    var taxIncluded: Boolean = false,
    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "class_fee",
        joinColumns = [JoinColumn(name = "fee_id")],
        inverseJoinColumns = [JoinColumn(name = "class_id")]
    )
    var classes: MutableList<EventClass> = mutableListOf(),
    @JsonIgnore var eventId: UUID? = null
)