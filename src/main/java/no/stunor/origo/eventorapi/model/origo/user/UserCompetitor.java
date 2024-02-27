package no.stunor.origo.eventorapi.model.origo.user;

import no.stunor.origo.eventorapi.model.origo.person.PersonName;


public record UserCompetitor(
    String personId,
    PersonName name,
    UserEntry personEntry,
    UserPersonStart personStart,
    UserTeamStart teamStart,
    UserPersonResult personResult,
    UserTeamResult teamResult) {
}
