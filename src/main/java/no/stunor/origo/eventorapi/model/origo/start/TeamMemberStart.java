package no.stunor.origo.eventorapi.model.origo.start;

import java.util.Date;

import no.stunor.origo.eventorapi.model.person.Competitor;

public record TeamMemberStart(
    Competitor person,
    int leg,
    Date startTime) {
}
