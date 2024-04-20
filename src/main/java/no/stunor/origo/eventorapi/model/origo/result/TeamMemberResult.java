package no.stunor.origo.eventorapi.model.origo.result;

import java.util.Date;
import java.util.List;

import no.stunor.origo.eventorapi.model.person.Competitor;

public record TeamMemberResult(
    Competitor person,
    int leg,
    Date startTime,
    Date finishTime,
    Result legResult,
    Result overallResult,
    List<SplitTime> splitTimes) {
}