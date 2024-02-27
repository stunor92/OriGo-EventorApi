package no.stunor.origo.eventorapi.model.origo.user;

import no.stunor.origo.eventorapi.model.origo.common.CCard;
import no.stunor.origo.eventorapi.model.origo.event.EventClass;

public record UserEntry(
    EventClass eventClass,
    CCard  cCard) {
}
