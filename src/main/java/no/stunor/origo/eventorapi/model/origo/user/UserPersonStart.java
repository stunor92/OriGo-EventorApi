package no.stunor.origo.eventorapi.model.origo.user;


import java.util.Date;

import no.stunor.origo.eventorapi.model.origo.event.EventClass;

public record UserPersonStart(
    Date startTime,
    String bib,
    EventClass eventClass) {
}
