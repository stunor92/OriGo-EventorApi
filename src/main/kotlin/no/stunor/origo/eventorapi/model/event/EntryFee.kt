package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.Instant

data class EntryFee (
        @JsonIgnore
        var id: String? = null,
        var entryFeeId: String? = null,
        var name: String = "",
        var price: Price? = null,
        var externalFee: Price? =null,
        var percentageSurcharge: Int? = null,
        var validFrom: Instant? = null,
        var validTo: Instant? = null,
        var fromBirthYear: Int? = null,
        var toBirthYear: Int? = null,
        var taxIncluded: Boolean = false,
        var eventClasses: List<String> = mutableListOf()
)
