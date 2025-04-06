package no.stunor.origo.eventorapi.model.event.competitor

import java.time.Instant

interface Competitor {
    var id: String?
    var name: Any
    var raceId: String
    var eventClassId: String
    var bib: String?
    var status: CompetitorStatus
    var startTime: Instant?
    var finishTime: Instant?
}
