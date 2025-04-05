package no.stunor.origo.eventorapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Eventor (
    @Id
    var eventorId: String = "",
    var name: String = "",
    var federation: String = "",
    var baseUrl: String = "",
    @JsonIgnore
    var apiKey: String = ""
)