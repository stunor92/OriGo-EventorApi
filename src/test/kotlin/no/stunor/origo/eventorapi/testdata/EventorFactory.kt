package no.stunor.origo.eventorapi.testdata

import no.stunor.origo.eventorapi.model.Eventor

class EventorFactory {
    companion object {
        fun createEventorNorway(): Eventor {
            return Eventor(
                id = "NOR",
                name = "Eventor Norge",
                federation = "Norges Orienteringsforbund",
                baseUrl = "https://eventor.orientering.no/api"
            )

        }
    }
}