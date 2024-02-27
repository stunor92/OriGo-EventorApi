package no.stunor.origo.eventorapi.model.origo.start;


import java.util.Date;
import java.util.List;

import no.stunor.origo.eventorapi.model.firestore.Organisation;

public record TeamStart(
    String startId,
    List<Organisation> organisations,
    List<TeamMemberStart> teamMembers,
    String teamName,
    Date startTime,
    String bib,
    String eventClassId) {
}
