package no.stunor.origo.eventorapi.model.origo.user;

import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import no.stunor.origo.eventorapi.model.Eventor;
import no.stunor.origo.eventorapi.model.origo.entry.EntryBreak;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRace{
    private Eventor eventor;
    private String eventId;
    private String eventName;
    private String raceId;
    private String raceName;
    private boolean canceled;
    private Date raceDate;
    private List<UserCompetitor> userCompetitors;
    private Map<String, Integer> organisationEntries;
    private List<EntryBreak> entryBreaks;
}
