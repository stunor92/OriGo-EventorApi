package no.stunor.origo.eventorapi.model.origo.start;

import java.util.Date;

import no.stunor.origo.eventorapi.model.origo.person.CompetitorPerson;

public record TeamMemberStart(
    CompetitorPerson person,
    int leg,
    Date startTime) {
}
