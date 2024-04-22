package no.stunor.origo.eventorapi.model.origo.entry;

import no.stunor.origo.eventorapi.model.event.CCard;
import no.stunor.origo.eventorapi.model.origo.CompetitorPerson;

public record TeamMemberEntry(
        CompetitorPerson person,
        int leg,
        CCard cCard) {
}
