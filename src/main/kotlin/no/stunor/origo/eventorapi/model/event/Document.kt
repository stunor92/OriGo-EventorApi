package no.stunor.origo.eventorapi.model.event

import com.fasterxml.jackson.annotation.JsonIgnore
import java.util.UUID

data class Document(
    var id: UUID? = null,
    var eventorRef: String = "",
    var name: String = "",
    var url: String = "",
    var type: String = "",
    @JsonIgnore var event: Event = Event()
)