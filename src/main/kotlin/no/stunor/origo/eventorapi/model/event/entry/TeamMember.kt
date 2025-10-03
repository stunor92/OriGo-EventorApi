package no.stunor.origo.eventorapi.model.event.entry

import io.hypersistence.utils.hibernate.type.array.ListArrayType
import jakarta.persistence.*
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName
import org.hibernate.annotations.Type
import java.io.Serializable
import java.sql.Timestamp


data class TeamMemberId(
        private val entryId: String,
        private val leg: Int?,
) : Serializable

@Entity
@IdClass(TeamMemberId::class)
data class TeamMember(
        @Id val entryId: String = "",
        @Id val leg: Int = 1,
        var personId: String? = null,
        var competitorId: String? = null,
        @Embedded var name: PersonName? = PersonName(),
        var birthYear: Int? = null,
        var nationality: String? = null,
        @Enumerated(EnumType.STRING) var gender: Gender? = null,
        @ManyToMany(cascade = [CascadeType.ALL])
        @JoinTable(name = "punching_unit_entry")
        var punchingUnits: List<PunchingUnit> = listOf(),
        var startTime: Timestamp? = null,
        var finishTime: Timestamp? = null,
        @Embedded var legResult: Result? = null,
        @Embedded var overallResult: Result? = null,
        @ManyToMany(cascade = [CascadeType.ALL])
        @JoinTable(name = "split_time")
        var splitTimes: List<SplitTime> = listOf(),
        @Type(value = ListArrayType::class) var feeIds: List<String> = listOf()
)