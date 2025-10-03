package no.stunor.origo.eventorapi.model.event.entry

import jakarta.persistence.*
import java.sql.Timestamp

@MappedSuperclass
abstract class EntryBase(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    open var entryId: String? = null,
    open var eventorId: String = "",
    open var eventId: String = "",
    open var raceId: String = "",
    open var classId: String = "",
    open var bib: String? = null,
    @Enumerated(EnumType.STRING) open var status: EntryStatus = EntryStatus.SignedUp,
    open var startTime: Timestamp? = null,
    open var finishTime: Timestamp? = null,
    @Embedded open var result: Result? = null
)

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "entry")
open class Entry(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    override var entryId: String? = null,
    override var eventorId: String = "",
    override var eventId: String = "",
    override var raceId: String = "",
    override var classId: String = "",
    override var bib: String? = null,
    @Enumerated(EnumType.STRING) override var status: EntryStatus = EntryStatus.SignedUp,
    override var startTime: Timestamp? = null,
    override var finishTime: Timestamp? = null,
    @Embedded override var result: Result? = null
) : EntryBase(
    entryId, eventorId, eventId, raceId, classId, bib, status, startTime, finishTime, result
)