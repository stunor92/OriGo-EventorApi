package no.stunor.origo.eventorapi.model.origo.user;

import no.stunor.origo.eventorapi.model.event.CCard;
import no.stunor.origo.eventorapi.model.event.EventClass;

public record UserEntry(
    EventClass eventClass,
    CCard  cCard) {
}
