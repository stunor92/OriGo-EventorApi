package no.stunor.origo.eventorapi.model.event.entrylist

import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName
import java.io.Serializable

data class TeamMemberEntry(
        var personId: String? = null,
        var name: PersonName? = null,
        var birthYear: Int? = null,
        var nationality: String? = null,
        var gender: Gender? = null,
        var punchingUnit: PunchingUnit? = null,
        var leg: Int = 1,
        var entryFeeIds: List<String> = listOf()
) : Serializable
