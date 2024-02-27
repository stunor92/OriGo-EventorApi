package no.stunor.origo.eventorapi.model.origo.user;


import java.util.Date;

import no.stunor.origo.eventorapi.model.origo.event.EventClass;

public record UserTeamStart(
    String teamName,
    Date startTime,
    String bib,
    int leg,
    EventClass eventClass) {

}
