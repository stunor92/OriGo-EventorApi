package no.stunor.origo.eventorapi.model.event

import jakarta.persistence.*
import java.io.Serializable
import java.sql.Timestamp

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
    @Id var eventorId: String = "",
    @Id var eventId: String = "",
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
    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(
        name = "class_fee",
        joinColumns = [JoinColumn(name = "event_id", referencedColumnName = "eventId"), JoinColumn(name = "eventor_id", referencedColumnName = "eventorId")],
        inverseJoinColumns = [JoinColumn(name = "class_id", referencedColumnName = "classId")]
    )
    var classes: MutableList<EventClass> = mutableListOf()
)