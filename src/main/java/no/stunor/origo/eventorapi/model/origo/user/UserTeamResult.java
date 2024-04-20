package no.stunor.origo.eventorapi.model.origo.user;

import no.stunor.origo.eventorapi.model.event.EventClass;
import no.stunor.origo.eventorapi.model.origo.result.Result;

public record UserTeamResult(
    String teamName,
    String bib,
    Result result,
    Integer leg,
    Integer legTime,
    EventClass eventClass) {

}
