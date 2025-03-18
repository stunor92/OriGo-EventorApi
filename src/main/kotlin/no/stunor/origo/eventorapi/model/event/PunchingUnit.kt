package no.stunor.origo.eventorapi.model.event



data class PunchingUnit (
        var id: String = "",
        var type: PunchingUnitType = PunchingUnitType.Other
)
