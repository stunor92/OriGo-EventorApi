package no.stunor.origo.eventorapi.model.event.competitor

import com.google.cloud.Timestamp

interface Competitor {
    var id: String?
    var name: Any
    var raceId: String
    var eventClassId: String
    var bib: String?
    var status: CompetitorStatus
    var startTime: Timestamp?
    var finishTime: Timestamp?
}
