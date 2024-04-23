package no.stunor.origo.eventorapi.model.origo.result;

import java.util.Date;
import java.util.List;

import no.stunor.origo.eventorapi.model.calendar.Result;
import no.stunor.origo.eventorapi.model.organisation.Organisation;
import no.stunor.origo.eventorapi.model.origo.CompetitorPerson;

public record PersonResult(
        String resultId,
        CompetitorPerson person,
        Organisation organisation,
        Date startTime,
        Date finishTime,
        Result result,
        List<SplitTime> splitTimes,
        String bib,
        String eventClassId) {
}