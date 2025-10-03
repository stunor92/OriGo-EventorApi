package no.stunor.origo.eventorapi.testdata

import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.person.*

class EventorFactory {
    companion object {
        fun createEventorNorway(): Eventor {
            return Eventor(
                eventorId = "NOR",
                name = "Eventor Norge",
                federation = "Norges Orienteringsforbund",
                baseUrl = "https://eventor.orientering.no/api"
            )

        }
    }
}