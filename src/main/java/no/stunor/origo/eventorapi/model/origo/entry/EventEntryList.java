package no.stunor.origo.eventorapi.model.origo.entry;

import java.util.List;

public record EventEntryList(List<PersonEntry> personEntryList,
                             List<TeamEntry> teamEntryList) {
}
