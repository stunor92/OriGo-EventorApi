package no.stunor.origo.eventorapi.model.origo.start;

import java.util.List;

public record RaceStartList (
    String raceId, 
    List<PersonStart> personStartList, 
    List<TeamStart> teamStartList){
}
