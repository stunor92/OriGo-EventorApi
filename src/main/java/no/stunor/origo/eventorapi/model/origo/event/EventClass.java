package no.stunor.origo.eventorapi.model.origo.event;

import java.util.List;

public record EventClass(String eventClassId,
                         String name,
                         String shortName,
                         EventClassTypeEnum classType,
                         Integer lowAge,
                         Integer highAge,
                         String sex,
                         boolean presentTime,
                         boolean orderedResult,
                         Integer legs,
                         Integer minAverageAge,
                         Integer highAverageAge,
                         List<String> entryFees) {
}
