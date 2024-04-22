package no.stunor.origo.eventorapi.model.origo.event;

import java.util.Date;
import java.util.List;

import no.stunor.origo.eventorapi.model.event.DistanceEnum;
import no.stunor.origo.eventorapi.model.event.LightConditionEnum;
import no.stunor.origo.eventorapi.model.event.Position;
import no.stunor.origo.eventorapi.model.calendar.UserCompetitor;

public record Race(String raceId,
                   String name,
                   LightConditionEnum lightCondition,
                   DistanceEnum distance,
                   Date date,
                   Position position,
                   Boolean startList,
                   Boolean resultList,
                   Boolean livelox,
                   List<UserCompetitor> competitors) {
}
