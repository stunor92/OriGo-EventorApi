package no.stunor.origo.eventorapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import java.io.Serializable

data class Eventor (
    var eventorId: String = "",
    var name: String = "",
    var federation: String = "",
    var baseUrl: String = "",
    @JsonIgnore
    var apiKey: String? = null
) : Serializable