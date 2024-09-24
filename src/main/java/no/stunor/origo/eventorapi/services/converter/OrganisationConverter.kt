package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.model.organisation.OrganisationType
import no.stunor.origo.eventorapi.model.organisation.Organisation
import org.springframework.stereotype.Component

@Component
class OrganisationConverter {

    fun convertOrganisation(organisation: org.iof.eventor.Organisation?): Organisation? {

        if(organisation == null)
            return null
        return Organisation(
            organisationId = if(organisation.organisationId != null) organisation.organisationId.content else null,
            name = organisation.name.content,
            type = convertOrganisationType(organisation),
            country = if (organisation.country != null) organisation.country.alpha3.value else null
        )
    }

    private fun convertOrganisationType(organisation: org.iof.eventor.Organisation): OrganisationType {
        return when (organisation.organisationTypeId.content) {
            "1" -> OrganisationType.Federation
            "2" -> OrganisationType.Region
            "5" -> OrganisationType.IOF
            else -> OrganisationType.Club
        }
    }
}