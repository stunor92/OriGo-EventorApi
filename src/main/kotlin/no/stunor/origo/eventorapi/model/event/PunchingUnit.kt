package no.stunor.origo.eventorapi.model.event

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import java.io.Serializable

data class PunchingUnitId(
        private val id: String,
        private val type: PunchingUnitType
) : Serializable {
        constructor() : this("", PunchingUnitType.Other)
}

@Entity
@IdClass(PunchingUnitId::class)
data class PunchingUnit (
        @Id var id: String = "",
        @Id var type: PunchingUnitType = PunchingUnitType.Other
)
