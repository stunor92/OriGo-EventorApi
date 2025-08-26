package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import org.hibernate.annotations.Type
import java.sql.Timestamp
import jakarta.persistence.*
import java.io.Serializable

data class FeeId(
        private val feeId: String,
        private val eventId: String,
        private val eventorId: String
) : Serializable {
        constructor() : this("", "", "")
}
@Entity
@IdClass(FeeId::class)
data class Fee (
        @JsonIgnore @Id var eventorId: String = "",
        @JsonIgnore @Id var eventId: String = "",
        @Id var feeId: String = "",
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
        @Type(value = ListArrayType::class) var classIds: List<String> = mutableListOf(),
        @ManyToOne
        @JoinColumns(
                JoinColumn(name = "eventId", referencedColumnName = "eventId", insertable = false, updatable = false),
                JoinColumn(name = "eventorId", referencedColumnName = "eventorId", insertable = false, updatable = false)
        )
        @JsonIgnore var event: Event? = null
)