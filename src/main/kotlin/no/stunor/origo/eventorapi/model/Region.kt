package no.stunor.origo.eventorapi.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.io.Serializable

data class RegionId(
        private val regionId: String,
        private val eventorId: String
) : Serializable {
        constructor() : this("", "")
}

@Entity
@IdClass(RegionId::class)
data class Region (
        @JsonIgnore @Id var eventorId: String = "",
        @Id var regionId: String = "",
        var name: String = ""
)