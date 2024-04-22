package no.stunor.origo.eventorapi.model.event.competitor

import com.google.cloud.Timestamp
import no.stunor.origo.eventorapi.model.event.CCard
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.origo.result.SplitTime
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName
import java.io.Serializable

data class TeamMemberCompetitor(
        var personId: String?,
        var name: PersonName = PersonName(),
        var organisation: Organisation? = null,
        var birthYear: Int? = null,
        var nationality: String = "",
        var gender: Gender = Gender.OTHER,
        var cCard: CCard? = null,
        var leg: Int = 1,
        var startTime: Timestamp? = null,
        var finishTime: Timestamp? = null,
        var legTime: Int? = null,
        var legTimeBehind: Int? =  null,
        val legPosition: Int? = null,
        var overallTime: Int? = null,
        var overallTimeBehind: Int? =  null,
        val overallPosition: Int? = null,
        val status: String = "OK",
        var splitTimes: List<SplitTime> = listOf()
) : Serializable