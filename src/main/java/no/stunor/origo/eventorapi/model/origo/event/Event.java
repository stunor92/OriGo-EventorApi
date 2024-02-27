package no.stunor.origo.eventorapi.model.origo.event;

import java.util.Date;
import java.util.List;

import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Organisation;
import no.stunor.origo.eventorapi.model.firestore.Region;
import no.stunor.origo.eventorapi.model.origo.entry.EntryBreak;

public record Event(Eventor eventor,
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
                    List<Race> races,
                    List<String> punchingUnitTypes,
                    List<String> webUrls,
                    String message,
                    String email,
                    String phone) {
}
