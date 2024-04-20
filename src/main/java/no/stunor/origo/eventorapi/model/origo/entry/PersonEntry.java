package no.stunor.origo.eventorapi.model.origo.entry;

import java.util.List;

import no.stunor.origo.eventorapi.model.organisation.Organisation;
import no.stunor.origo.eventorapi.model.origo.common.CCard;
import no.stunor.origo.eventorapi.model.person.Competitor;

public record PersonEntry(
    String entryId,
    Competitor person,
    Organisation organisation,
    CCard cCard,
    String bib,
    List<String> raceIds,
    List<String> entryFeeIds,
    String eventClassId) {
}
