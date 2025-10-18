package no.stunor.origo.eventorapi.model.event.entry

import java.sql.Timestamp

abstract class Entry(
    open var entryId: String,
    open var raceId: String,
    open var classId: String,
    open var bib: String?,
    open var status: EntryStatus,
    open var startTime: Timestamp?,
    open var finishTime: Timestamp?,
    open var result: Result?
)