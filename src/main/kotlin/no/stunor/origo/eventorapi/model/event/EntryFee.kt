package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.DocumentId
import java.io.Serializable

data class EntryFee (
        @JsonIgnore
        @DocumentId
        var id: String? = null,
        var entryFeeId: String? = null,
        var name: String = "",
        var price: Price? = null,
        var externalFee: Price? =null,
        var percentageSurcharge: Int? = null,
        var validFrom: Timestamp? = null,
        var validTo: Timestamp? = null,
        var fromBirthYear: Int? = null,
        var toBirthYear: Int? = null,
        var taxIncluded: Boolean = false,
        var eventClasses: List<String> = mutableListOf()
) : Serializable