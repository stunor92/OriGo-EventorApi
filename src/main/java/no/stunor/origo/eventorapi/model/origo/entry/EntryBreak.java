package no.stunor.origo.eventorapi.model.origo.entry;

import com.google.cloud.Timestamp;

import java.util.Date;

public record EntryBreak(Timestamp from,
                         Timestamp to) {
}
