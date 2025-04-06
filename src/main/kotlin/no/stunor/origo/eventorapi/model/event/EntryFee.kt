package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import java.time.ZonedDateTime


data class EntryFee (
        @JsonIgnore
        var id: String? = null,
        var entryFeeId: String? = null,
        var name: String = "",
        var price: Price? = null,
        var externalFee: Price? =null,
        var percentageSurcharge: Int? = null,
        var validFrom: ZonedDateTime? = null,
        var validTo: ZonedDateTime? = null,
        var fromBirthYear: Int? = null,
        var toBirthYear: Int? = null,
        var taxIncluded: Boolean = false,
        var eventClasses: List<String> = mutableListOf()
)
