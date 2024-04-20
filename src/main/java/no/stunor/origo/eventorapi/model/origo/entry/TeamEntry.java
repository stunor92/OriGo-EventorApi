package no.stunor.origo.eventorapi.model.origo.entry;

import java.util.List;

import no.stunor.origo.eventorapi.model.organisation.Organisation;

public record TeamEntry(
    String entryId,
    List<Organisation> organisations,
    List<TeamMemberEntry> teamMembers,
    String teamName,
    String bib,
    List<String> entryFeeIds,
    List<String> eventClassIds) {

}
