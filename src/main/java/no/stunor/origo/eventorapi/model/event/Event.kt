package no.stunor.origo.eventorapi.model.event;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.cloud.firestore.annotation.DocumentId;
import com.google.cloud.firestore.annotation.Exclude;

import no.stunor.origo.eventorapi.model.Eventor;
import no.stunor.origo.eventorapi.model.organisation.Organisation;
import no.stunor.origo.eventorapi.model.Region;
import no.stunor.origo.eventorapi.model.origo.entry.EntryBreak;
import no.stunor.origo.eventorapi.model.origo.event.DisiplineEnum;
import no.stunor.origo.eventorapi.model.origo.event.Document;
import no.stunor.origo.eventorapi.model.origo.event.EventClass;
import no.stunor.origo.eventorapi.model.origo.event.EventClassificationEnum;
import no.stunor.origo.eventorapi.model.origo.event.EventFormEnum;
import no.stunor.origo.eventorapi.model.origo.event.EventStatusEnum;
import no.stunor.origo.eventorapi.model.origo.event.Race;

public record Event(Eventor eventor,
                    @JsonIgnore
                    @DocumentId
                    String id,
                    String eventId,
                    String name,
                    EventFormEnum type,
                    EventClassificationEnum classification,
                    EventStatusEnum status,
                    List<DisiplineEnum> disciplines,
                    Date startDate,
                    Date finishDate,
                    List<Organisation> organisers,
                    List<Region> regions,
                    List<EventClass> eventClasses,
                    List<Document> documents,
                    List<EntryBreak> entryBreaks,
                    @Exclude
                    List<Race> races,
                    List<String> punchingUnitTypes,
                    List<String> webUrls,
                    String message,
                    String email,
                    String phone) {
}
