package no.stunor.origo.eventorapi.model.event.competitor

import com.google.cloud.Timestamp
import java.io.Serializable


interface Competitor : Serializable {
    var id: String?
    var name: Any
    var raceId: String
    var eventClassId: String
    var bib: String?
    var status: CompetitorStatus
    var startTime: Timestamp?
    var finishTime: Timestamp?
}