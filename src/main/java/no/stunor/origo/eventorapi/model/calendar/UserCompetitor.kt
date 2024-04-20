package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.origo.user.*
import no.stunor.origo.eventorapi.model.person.PersonName
import java.io.Serializable


data class UserCompetitor(
        var personId: String = "",
        var name: PersonName = PersonName(),
        var personEntry: UserEntry? = null,
        var personStart: UserPersonStart? = null,
        var teamStart: UserTeamStart? = null,
        var personResult: UserPersonResult? = null,
        var teamResult: UserTeamResult? = null
) : Serializable