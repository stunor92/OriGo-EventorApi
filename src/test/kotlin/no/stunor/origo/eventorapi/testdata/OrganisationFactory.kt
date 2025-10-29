package no.stunor.origo.eventorapi.testdata

import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.organisation.OrganisationType

class OrganisationFactory {
    companion object {
        fun createTestOrganisation(): Organisation {
            return Organisation(
                eventorId = "NOR",
                eventorRef = "141",
                name = "Testklubben",
                type = OrganisationType.Club,
                country = "NOR",
                region = Region(
                    eventorRef = "8",
                    name = "Hordaland"
                ),

            )
        }
    }
}