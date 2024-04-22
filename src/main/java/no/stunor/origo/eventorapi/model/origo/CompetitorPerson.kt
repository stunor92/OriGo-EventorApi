package no.stunor.origo.eventorapi.model.origo

import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName

data class CompetitorPerson (
        var eventorId: String = "",
        var personId: String?,
        var name: PersonName = PersonName(),
        var birthYear: Int = 0,
        var nationality: String = "",
        var gender: Gender = Gender.OTHER
)