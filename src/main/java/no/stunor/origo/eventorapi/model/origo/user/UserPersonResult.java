package no.stunor.origo.eventorapi.model.origo.user;


import no.stunor.origo.eventorapi.model.origo.event.EventClass;
import no.stunor.origo.eventorapi.model.origo.result.Result;

public record UserPersonResult(
    String bib,
    Result result,
    EventClass eventClass) {
}
