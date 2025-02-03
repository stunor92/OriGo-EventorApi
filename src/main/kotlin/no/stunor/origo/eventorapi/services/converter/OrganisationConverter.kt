package no.stunor.origo.eventorapi.services.converter

import org.springframework.stereotype.Component

@Component
class OrganisationConverter {


    fun convertOrganisationId(organisation: org.iof.eventor.Organisation?): String? {
        if(organisation == null || organisation.organisationId == null)
            return null
        return  organisation.organisationId.content
    }

    fun convertOrganisationId(organisationId: org.iof.eventor.OrganisationId?): String? {
        if(organisationId == null)
            return null
        return  organisationId.content
    }

}