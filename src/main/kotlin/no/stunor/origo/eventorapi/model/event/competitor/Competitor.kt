package no.stunor.origo.eventorapi.model.event.competitor

import java.sql.Timestamp

interface Competitor {
    var id: String?
    var name: Any
    var raceId: String
    var classId: String
    var bib: String?
    var status: CompetitorStatus
    var startTime: Timestamp?
    var finishTime: Timestamp?
}
