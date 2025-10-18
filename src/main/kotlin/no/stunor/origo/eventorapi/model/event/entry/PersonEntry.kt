package no.stunor.origo.eventorapi.model.event.entry

import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName
import java.sql.Timestamp
import java.util.UUID

data class PersonEntry (
    override var entryId: String = UUID.randomUUID().toString(),
    override var raceId: String = "",
    var name: PersonName = PersonName(),
    var competitorId: String? = null,
    var personId: String? = null,
    var organisation: Organisation? = null,
    var birthYear: Int? = null,
    var nationality: String? = null,
    var gender: Gender = Gender.Other,
    override var classId: String = "",
    override var bib: String? = null,
    var punchingUnits: MutableList<PunchingUnit> = mutableListOf(),
    override var status: EntryStatus,
    override var startTime: Timestamp? = null,
    override var finishTime: Timestamp? = null,
    override var result: Result? = null,
    var splitTimes: MutableList<SplitTime> = mutableListOf(),
) : Entry(
        entryId, raceId, classId, bib, status, startTime, finishTime, result
)
