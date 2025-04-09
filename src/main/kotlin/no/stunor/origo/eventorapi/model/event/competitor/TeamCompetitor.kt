package no.stunor.origo.eventorapi.model.event.competitor

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import no.stunor.origo.eventorapi.config.TimestampISO8601Serializer
import java.sql.Timestamp

data class TeamCompetitor(
        @JsonIgnore
        override var id: String? = null,
        override var raceId: String = "",
        override var classId: String = "",
        var organisationIds: List<String> = listOf(),
        var teamMembers: List<TeamMemberCompetitor> = listOf(),
        override var name: Any = "",
        override var bib: String? = null,
        override var status: CompetitorStatus,
        @JsonSerialize(using = TimestampISO8601Serializer::class) override var startTime: Timestamp? = null,
        @JsonSerialize(using = TimestampISO8601Serializer::class) override var finishTime: Timestamp? = null,
        var result: Result? = null,
) : Competitor {
        override fun equals(other: Any?): Boolean {
                return if(other is TeamCompetitor){
                        raceId == other.raceId && name == other.name
                } else{
                        false
                }
        }

        override fun hashCode(): Int {
                var result = id?.hashCode() ?: 0
                (31 * result + raceId.hashCode()).also { result = it }
                (31 * result + classId.hashCode()).also { result = it }
                (31 * result + organisationIds.hashCode()).also { result = it }
                result = 31 * result + teamMembers.hashCode()
                result = 31 * result + name.hashCode()
                result = 31 * result + (bib?.hashCode() ?: 0)
                result = 31 * result + (startTime?.hashCode() ?: 0)
                result = 31 * result + (finishTime?.hashCode() ?: 0)
                result = 31 * result + result.hashCode()
                return result
        }
}