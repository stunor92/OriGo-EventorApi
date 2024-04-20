package no.stunor.origo.eventorapi.model.origo.result;


import java.util.Date;
import java.util.List;

import no.stunor.origo.eventorapi.model.organisation.Organisation;

public record TeamResult(
    String resultId,
    List<Organisation> organisations,
    List<TeamMemberResult> teamMembers,
    String teamName,
    Date startTime,
    Date finishTime,
    Result result,
    String bib,
    String eventClassId) {
}