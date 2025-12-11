package no.stunor.origo.eventorapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.*

data class Region (
    var id: UUID? = null,
    @JsonIgnore var eventorId: String = "",
    var eventorRef: String = "",
    var name: String = "",
)