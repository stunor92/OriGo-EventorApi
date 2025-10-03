package no.stunor.origo.eventorapi.model.event.entry

import com.fasterxml.jackson.annotation.JsonIgnore
import io.hypersistence.utils.hibernate.type.array.ListArrayType
import jakarta.persistence.*
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName
import org.hibernate.annotations.Type
import java.sql.Timestamp
import kotlin.jvm.Transient

@Entity
data class PersonEntry (
        @JsonIgnore
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        override var entryId: String? = null,
        @JsonIgnore override var eventorId: String = "",
        @JsonIgnore override var eventId: String = "",
        override var raceId: String = "",
        @Embedded var name: PersonName = PersonName(),
        var competitorId: String? = null,
        var personId: String? = null,
        @ManyToMany(cascade = [CascadeType.ALL])
        @JoinTable(
                name = "entry_organisation",
                joinColumns = [JoinColumn(name = "entry_id", referencedColumnName = "entryId"), JoinColumn(name = "eventor_id", referencedColumnName = "eventorId")],
                inverseJoinColumns = [JoinColumn(name = "organisation_id", referencedColumnName = "organisationId")]
        )
        @JsonIgnore var organisations: List<Organisation> = listOf(),
        @Transient var organisation: Organisation? = organisations.firstOrNull(),
        var birthYear: Int? = null,
        var nationality: String? = null,
        @Enumerated(EnumType.STRING) var gender: Gender = Gender.Other,
        override var classId: String = "",
        override var bib: String? = null,
        @ManyToMany(cascade = [CascadeType.ALL])
        @JoinTable(name = "punching_unit_entry",)
        var punchingUnits: List<PunchingUnit> = listOf(),
        @Enumerated(EnumType.STRING) override var status: EntryStatus,
        override var startTime: Timestamp? = null,
        override var finishTime: Timestamp? = null,
        @Embedded override var result: Result? = null,
        @ManyToMany(cascade = [CascadeType.ALL])
        @JoinTable(name = "split_time",)
        var splitTimes: List<SplitTime> = listOf(),
        @Type(value = ListArrayType::class) var feeIds: List<String> = listOf(),
) : Entry(
        entryId, eventorId, eventId, raceId, classId, bib, status, startTime, finishTime, result
)
