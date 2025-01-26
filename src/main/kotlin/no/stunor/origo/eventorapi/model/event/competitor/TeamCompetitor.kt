package no.stunor.origo.eventorapi.model.event.competitor

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.DocumentId
import no.stunor.origo.eventorapi.model.organisation.Organisation

data class TeamCompetitor(
        @JsonIgnore
        @DocumentId
        override var id: String? = null,
        override var raceId: String = "",
        override var eventClassId: String = "",
        var organisations: List<Organisation> = listOf(),
        var organisationIds: List<String> = listOf(),
        var teamMembers: List<TeamMemberCompetitor> = listOf(),
        override var name: Any = "",
        override var bib: String? = null,
        override var status: CompetitorStatus,
        override var startTime: Timestamp? = null,
        override var finishTime: Timestamp? = null,
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
                result = 31 * result + raceId.hashCode()
                result = 31 * result + eventClassId.hashCode()
                result = 31 * result + organisationIds.hashCode()
                result = 31 * result + teamMembers.hashCode()
                result = 31 * result + name.hashCode()
                result = 31 * result + (bib?.hashCode() ?: 0)
                result = 31 * result + (startTime?.hashCode() ?: 0)
                result = 31 * result + (finishTime?.hashCode() ?: 0)
                result = 31 * result + result.hashCode()
                return result
        }
}