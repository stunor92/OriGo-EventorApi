package no.stunor.origo.eventorapi.model.event.competitor

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.cloud.Timestamp
import com.google.cloud.firestore.annotation.DocumentId
import no.stunor.origo.eventorapi.model.event.PunchingUnit
import no.stunor.origo.eventorapi.model.organisation.SimpleOrganisation
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.PersonName

data class PersonCompetitor(
        @DocumentId
        override var origoId: String? = null,
        override var raceId: String = "",
        override var eventClassId: String = "",
        var personId: String? = null,
        var name: PersonName = PersonName(),
        var organisation: SimpleOrganisation? = null,
        var birthYear: Int? = null,
        var nationality: String? = null,
        var gender: Gender = Gender.Other,
        var punchingUnit: PunchingUnit? = null,
        override var bib: String? = null,
        override var startTime: Timestamp? = null,
        override var finishTime: Timestamp? = null,
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
                var result = origoId?.hashCode() ?: 0
                result = 31 * result + raceId.hashCode()
                result = 31 * result + eventClassId.hashCode()
                result = 31 * result + (personId?.hashCode() ?: 0)
                result = 31 * result + name.hashCode()
                result = 31 * result + (organisation?.hashCode() ?: 0)
                result = 31 * result + (birthYear ?: 0)
                result = 31 * result + (nationality?.hashCode() ?: 0)
                result = 31 * result + gender.hashCode()
                result = 31 * result + (punchingUnit?.hashCode() ?: 0)
                result = 31 * result + (bib?.hashCode() ?: 0)
                result = 31 * result + (startTime?.hashCode() ?: 0)
                result = 31 * result + (finishTime?.hashCode() ?: 0)
                result = 31 * result + result.hashCode()
                result = 31 * result + splitTimes.hashCode()
                result = 31 * result + entryFeeIds.hashCode()
                return result
        }
}