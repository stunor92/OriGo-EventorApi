package no.stunor.origo.eventorapi.model.origo.entry;

import java.util.List;

import no.stunor.origo.eventorapi.model.firestore.Organisation;
import no.stunor.origo.eventorapi.model.origo.common.CCard;
import no.stunor.origo.eventorapi.model.origo.person.CompetitorPerson;

public record PersonEntry(
    String entryId,
    CompetitorPerson person,
    Organisation organisation,
    CCard cCard,
    String bib,
    List<String> raceIds,
    List<String> entryFeeIds,
    String eventClassId) {
}
