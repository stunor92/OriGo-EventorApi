package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import java.sql.Timestamp
import java.util.UUID

data class Fee (
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
    var classes: MutableList<EventClass> = mutableListOf(),
    @JsonIgnore var eventId: UUID? = null
)