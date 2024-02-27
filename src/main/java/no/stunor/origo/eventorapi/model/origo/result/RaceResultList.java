package no.stunor.origo.eventorapi.model.origo.result;

import java.util.List;

public record RaceResultList(
    String raceId,
    List<PersonResult> personResultList, 
    List<TeamResult> teamResultList){
}
