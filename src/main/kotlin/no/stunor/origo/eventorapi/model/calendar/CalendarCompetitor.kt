package no.stunor.origo.eventorapi.model.calendar

import no.stunor.origo.eventorapi.model.person.PersonName

data class CalendarCompetitor(
        var personId: String = "",
        var name: PersonName = PersonName(),
        var personEntry: CalendarEntry? = null,
        var personStart: CalendarPersonStart? = null,
        var teamStart: CalendarTeamStart? = null,
        var personResult: CalendarPersonResult? = null,
        var teamResult: CalendarTeamResult? = null
)
