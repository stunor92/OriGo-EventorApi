package no.stunor.origo.eventorapi.model.origo.calendar;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Organisation;
import no.stunor.origo.eventorapi.model.origo.common.Position;
import no.stunor.origo.eventorapi.model.origo.entry.EntryBreak;
import no.stunor.origo.eventorapi.model.origo.event.DisiplineEnum;
import no.stunor.origo.eventorapi.model.origo.event.DistanceEnum;
import no.stunor.origo.eventorapi.model.origo.event.EventClassificationEnum;
import no.stunor.origo.eventorapi.model.origo.event.EventFormEnum;
import no.stunor.origo.eventorapi.model.origo.event.EventStatusEnum;
import no.stunor.origo.eventorapi.model.origo.event.LightConditionEnum;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarRace{
    private Eventor eventor;
    private String eventId;
    private String raceId;
    private String eventName;
    private String raceName;
    private Date raceDate;
    private EventFormEnum type;
    private EventClassificationEnum classification;
    private LightConditionEnum lightCondition;
    private DistanceEnum distance;
    private Position position;
    private EventStatusEnum status;
    private List<DisiplineEnum> disciplines;
    private List<Organisation> organisers;
    private List<EntryBreak> entryBreaks;
    private boolean signedUp;
    private int entries;
    private Map<String,Integer> organisationEntries;
    private boolean startList;
    private boolean resultList;
    private boolean livelox;
}
