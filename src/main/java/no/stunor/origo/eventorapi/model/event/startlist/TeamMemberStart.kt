package no.stunor.origo.eventorapi.model.event.startlist

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName
import java.io.Serializable

data class TeamMemberStart(
        var personId: String? = null,
        var name: PersonName? = null,
        var birthYear: Int? = null,
        var nationality: String? = null,
        var gender: Gender? = null,
        var punchingUnit: PunchingUnit? = null,
        var leg: Int = 1,
        var startTime: Timestamp? = null,
) : Serializable
