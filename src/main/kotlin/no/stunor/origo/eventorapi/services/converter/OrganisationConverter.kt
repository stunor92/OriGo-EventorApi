package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.data.RegionRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.organisation.OrganisationType
import org.springframework.stereotype.Component

@Component
class OrganisationConverter(
    var organisationRepository: OrganisationRepository,
    var regionRepository: RegionRepository
) {

    fun convertOrganisations(organisations: List<Any>, eventor: Eventor): MutableList<Organisation> {
        val result = mutableListOf<Organisation>()
        for (organisation in organisations) {
            convertOrganisation(organisation, eventor)?.let { result.add(it) }
        }
        return result
    }

    fun convertOrganisation(organisation: Any?, eventor: Eventor): Organisation? {
        if (organisation == null) {
            return null
        }
        return when (organisation) {
            is org.iof.eventor.Organisation -> convertOrganisation(organisation, eventor)
            is org.iof.eventor.OrganisationId -> organisationRepository.findByOrganisationIdAndEventorId(organisation.content, eventor.eventorId)
            is String -> organisationRepository.findByOrganisationIdAndEventorId(organisation, eventor.eventorId)

            else -> null
        }
    }

    private fun convertOrganisation(organisation: org.iof.eventor.Organisation, eventor: Eventor): Organisation? {
        if (organisation.organisationId == null) {
            return null
        }
        return Organisation(
            organisationId = organisation.organisationId.content,
            eventorId = eventor.eventorId,
            name = organisation.name.content,
            type = convertOrganisationType(organisation),
            country = if(organisation.country != null
                && organisation.country.alpha3 != null
                && organisation.country.alpha3.value.length == 3) {
                organisation.country.alpha3.value
            } else
                eventor.eventorId,
            region = if (organisation.parentOrganisation != null)
                organisation.parentOrganisation.organisationId?.content?.let {
                regionRepository.findByRegionIdAndEventorId(it, eventor.eventorId) ?:
                regionRepository.findByRegionIdAndEventorId(organisation.organisationId.content, eventor.eventorId)
            }
            else
                regionRepository.findByRegionIdAndEventorId(organisation.organisationId.content, eventor.eventorId)
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
