package no.stunor.origo.eventorapi.model

import com.fasterxml.jackson.annotation.JsonIgnore

data class Eventor (
    var id: String = "",
    var name: String = "",
    var federation: String = "",
    var baseUrl: String = "",
    @JsonIgnore var eventorApiKey: String = ""
)