package no.stunor.origo.eventorapi.model.event.competitor

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.DocumentId
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName

data class PersonCompetitor(
        @JsonIgnore
        @DocumentId
        override var id: String = "",
        override var eventorId: String = "",
        override var eventId: String = "",
        override var raceId: String = "",
        override var eventClassId: String = "",
        var personId: String?,
        var name: PersonName = PersonName(),
        var organisation: Organisation? = null,
        var birthYear: Int? = null,
        var nationality: String? = null,
        var gender: Gender = Gender.Other,
        var punchingUnit: PunchingUnit? = null,
        override var bib: String? = null,
        override var startTime: Timestamp? = null,
        override var finishTime: Timestamp? = null,
        var result: Result? = null,
        var splitTimes: List<SplitTime> = listOf(),
        var entryFeeIds: List<String> = listOf()
) : Competitor