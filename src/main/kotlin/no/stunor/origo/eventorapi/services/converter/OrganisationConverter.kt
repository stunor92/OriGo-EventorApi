package no.stunor.origo.eventorapi.services.converter

import org.springframework.stereotype.Component

@Component
class OrganisationConverter {

    fun convertOrganisationIds(organisations: List<Any>): List<String> {
        val organisationIds = mutableListOf<String>()
        for (organisation in organisations) {
            convertOrganisationId(organisation)?.let { organisationIds.add(it) }
        }
        return organisationIds
    }

    fun convertOrganisationId(organisation: Any?): String? {
        if (organisation == null) {
            return null
        }
        return when (organisation) {
            is org.iof.eventor.Organisation -> organisation.organisationId?.content
            is org.iof.eventor.OrganisationId -> organisation.content
            else -> null
        }
    }
}
