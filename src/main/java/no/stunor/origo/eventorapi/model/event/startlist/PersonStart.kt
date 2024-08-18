package no.stunor.origo.eventorapi.model.event.startlist

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.DocumentId
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName

data class PersonStart(
        override var raceId: String = "",
        override var eventClassId: String = "",
        var personId: String? = null,
        override var name: Any = PersonName(),
        var organisation: Organisation? = null,
        var birthYear: Int? = null,
        var nationality: String? = null,
        var gender: Gender = Gender.Other,
        var punchingUnit: PunchingUnit? = null,
        override var bib: String? = null,
        override var startTime: Timestamp? = null
) : CompetitorStart