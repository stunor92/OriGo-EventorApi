package no.stunor.origo.eventorapi.model.event.entrylist

import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName

data class PersonEntry(
        override var raceId: String = "",
        override var eventClassId: String = "",
        var personId: String? = null,
        override var name: Any = PersonName(),
        var organisation: Organisation? = null,
        var birthYear: Int? = null,
        var nationality: String? = null,
        var gender: Gender = Gender.Other,
        var punchingUnit: PunchingUnit? = null,
        var entryFeeIds: List<String> = listOf()
) : CompetitorEntry