package no.stunor.origo.eventorapi.model.person

data class Competitor (
        var eventorId: String = "",
        var personId: String?,
        var name: PersonName = PersonName(),
        var birthYear: Int = 0,
        var nationality: String = "",
        var gender: Gender = Gender.OTHER
)