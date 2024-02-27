package no.stunor.origo.eventorapi.model.origo.entry;

import no.stunor.origo.eventorapi.model.origo.common.CCard;
import no.stunor.origo.eventorapi.model.origo.person.CompetitorPerson;

public record TeamMemberEntry(
    CompetitorPerson person,
    int leg,
    CCard cCard) {
}
