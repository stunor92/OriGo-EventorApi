package no.stunor.origo.eventorapi.model.event.entry

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import no.stunor.origo.eventorapi.model.organisation.Organisation
import java.sql.Timestamp


@Entity
class TeamEntry (
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var entryId: String? = null,
    override var eventorId: String = "",
    override var eventId: String = "",
    override var raceId: String = "",
    var name: String = "",
    @ManyToMany
    @JoinTable(name = "entry_organisation")
    var organisations: List<Organisation> = listOf(),
    @ManyToMany(cascade = [CascadeType.ALL])
    @JoinTable(name = "team_member",)
    var teamMembers: List<TeamMember> = listOf(),
    override var classId: String = "",
    override var bib: String? = null,
    @Enumerated(EnumType.STRING) override var status: EntryStatus,
    override var startTime: Timestamp? = null,
    override var finishTime: Timestamp? = null,
    @Embedded override var result: Result? = null
) :  Entry(
    entryId, eventorId, eventId, raceId, classId, bib, status, startTime, finishTime, result
)