package no.stunor.origo.eventorapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.Id

@Entity
data class Eventor (
    @Id var eventorId: String = "",
    var name: String = "",
    var federation: String = "",
    var baseUrl: String = "",
    @JsonIgnore var eventorApiKey: String = ""
){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Eventor) return false

        return eventorId == other.eventorId &&
                name == other.name &&
                federation == other.federation &&
                baseUrl == other.baseUrl &&
                eventorApiKey == other.eventorApiKey
    }

    override fun hashCode(): Int {
        var result = eventorId.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + federation.hashCode()
        result = 31 * result + baseUrl.hashCode()
        result = 31 * result + eventorApiKey.hashCode()
        return result
    }}