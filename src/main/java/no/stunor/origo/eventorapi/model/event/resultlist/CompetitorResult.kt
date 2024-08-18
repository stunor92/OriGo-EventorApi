package no.stunor.origo.eventorapi.model.event.resultlist

import com.google.cloud.Timestamp
import java.io.Serializable


interface CompetitorResult : Serializable {
    var name: Any
    var raceId: String
    var eventClassId: String
    var bib: String?
    var startTime: Timestamp?
    var finishTime: Timestamp?
}