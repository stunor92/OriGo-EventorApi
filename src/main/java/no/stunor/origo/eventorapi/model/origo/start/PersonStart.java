package no.stunor.origo.eventorapi.model.origo.start;

import java.util.Date;

import no.stunor.origo.eventorapi.model.organisation.Organisation;
import no.stunor.origo.eventorapi.model.origo.CompetitorPerson;

public record PersonStart(
        String startId,
        CompetitorPerson person,
        Organisation organisation,
        Date startTime,
        String bib,
        String eventClassId) {
}
