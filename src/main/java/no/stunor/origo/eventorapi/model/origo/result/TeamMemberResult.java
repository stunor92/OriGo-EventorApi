package no.stunor.origo.eventorapi.model.origo.result;

import java.util.Date;
import java.util.List;

import no.stunor.origo.eventorapi.model.origo.person.CompetitorPerson;

public record TeamMemberResult(
    CompetitorPerson person,
    int leg,
    Date startTime,
    Date finishTime,
    Result legResult,
    Result overallResult,
    List<SplitTime> splitTimes) {
}