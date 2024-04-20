package no.stunor.origo.eventorapi.model.origo.start;

import java.util.Date;

import no.stunor.origo.eventorapi.model.organisation.Organisation;
import no.stunor.origo.eventorapi.model.person.Competitor;

public record PersonStart(
    String startId,
    Competitor person,
    Organisation organisation,
    Date startTime,
    String bib,
    String eventClassId) {
}
