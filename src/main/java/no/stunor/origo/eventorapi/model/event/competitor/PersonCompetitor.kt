package no.stunor.origo.eventorapi.model.event.competitor

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.event.CCard
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.origo.result.SplitTime
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName

data class PersonCompetitor(
        override var eventorId: String = "",
        override var eventId: String = "",
        override var raceId: String = "",
        override var eventClassId: String = "",
        var personId: String?,
        var name: PersonName = PersonName(),
        var organisation: Organisation? = null,
        var birthYear: Int? = null,
        var nationality: String = "",
        var gender: Gender = Gender.OTHER,
        var cCard: CCard? =  null,
        override var bib: String? = null,
        override var startTime: Timestamp? = null,
        override var finishTime: Timestamp? = null,
        var time: Int? = null,
        var timeBehind: Int? =  null,
        val position: Int? = null,
        val status: String = "OK",
        var splitTimes: List<SplitTime> = listOf(),
        override var entryFeeIds: List<String> = listOf()
) : Competitor