package no.stunor.origo.eventorapi.model.event.entry

import no.stunor.origo.eventorapi.model.organisation.Organisation
import java.sql.Timestamp
import java.util.UUID


class TeamEntry (
    override var entryId: String = UUID.randomUUID().toString(),
    override var raceId: String = "",
    var name: String = "",
    var organisations: MutableList<Organisation> = mutableListOf(),
    var teamMembers: MutableList<TeamMember> = mutableListOf(),
    override var classId: String = "",
    override var bib: String? = null,
    override var status: EntryStatus,
    override var startTime: Timestamp? = null,
    override var finishTime: Timestamp? = null,
    override var result: Result? = null
) :  Entry(
    entryId, raceId, classId, bib, status, startTime, finishTime, result
)