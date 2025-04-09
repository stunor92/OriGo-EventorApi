package no.stunor.origo.eventorapi.model.event.competitor

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.stunor.origo.eventorapi.config.TimestampISO8601Serializer
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName
import java.sql.Timestamp

data class PersonCompetitor(
        @JsonIgnore
        override var id: String? = null,
        override var raceId: String = "",
        override var classId: String = "",
        var personId: String? = null,
        override var name: Any = PersonName(),
        var organisationId: String? = null,
        var birthYear: Int? = null,
        var nationality: String? = null,
        var gender: Gender = Gender.Other,
        var punchingUnits: List<PunchingUnit> = listOf(),
        override var bib: String? = null,
        override var status: CompetitorStatus,
        @JsonSerialize(using = TimestampISO8601Serializer::class) override var startTime: Timestamp? = null,
        @JsonSerialize(using = TimestampISO8601Serializer::class) override var finishTime: Timestamp? = null,
        var result: Result? = null,
        var splitTimes: List<SplitTime> = listOf(),
        var entryFeeIds: List<String> = listOf()

) : Competitor {
        override fun equals(other: Any?): Boolean {
                return if(other is PersonCompetitor){
                        raceId == other.raceId && personId == other.personId
                } else{
                        false
                }
        }

        override fun hashCode(): Int {
                var result = id?.hashCode() ?: 0
                (31 * result + raceId.hashCode()).also { result = it }
                (31 * result + classId.hashCode()).also { result = it }
                (31 * result + (personId?.hashCode() ?: 0)).also { result = it }
                (31 * result + name.hashCode()).also { result = it }
                (31 * result + (organisationId?.hashCode() ?: 0)).also { result = it }
                (31 * result + (birthYear ?: 0)).also { result = it }
                (31 * result + (nationality?.hashCode() ?: 0)).also { result = it }
                result = 31 * result + gender.hashCode()
                result = 31 * result + (bib?.hashCode() ?: 0)
                result = 31 * result + (startTime?.hashCode() ?: 0)
                result = 31 * result + (finishTime?.hashCode() ?: 0)
                result = 31 * result + result.hashCode()
                result = 31 * result + splitTimes.hashCode()
                result = 31 * result + entryFeeIds.hashCode()
                return result
        }
}
