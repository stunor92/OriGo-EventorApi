package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.api.exception.EventorNotFoundException
import no.stunor.origo.eventorapi.api.exception.OrganisationNotFoundException
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.OrganisationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrganisationService {
    @Autowired
    private lateinit var eventorRepository: EventorRepository

    @Autowired
    private lateinit var organisationRepository: OrganisationRepository

    @Autowired
    private lateinit var eventorService: EventorService


    fun validateApiKey(eventorId: String, organisationId: String): Boolean {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val organisation = organisationRepository.findByOrganisationIdAndEventorId(organisationId, eventorId) ?: throw OrganisationNotFoundException()
        if (organisation.apiKey == null) {
            return false
        }

        val eventorOrganisation = eventorService.getOrganisationFromApiKey(eventor.baseUrl, organisation.apiKey)
        if (eventorOrganisation == null || eventorOrganisation.organisationId == null) {
            return false
        }
        return organisation.organisationId == eventorOrganisation.organisationId.content
    }

    fun updateApiKey(eventorId: String, organisationId: String, apiKey: String): Boolean {
        val eventor = eventorRepository.findByEventorId(eventorId) ?: throw EventorNotFoundException()
        val organisation = organisationRepository.findByOrganisationIdAndEventorId(organisationId, eventorId) ?: throw OrganisationNotFoundException()

        val eventorOrganisation = eventorService.getOrganisationFromApiKey(eventor.baseUrl, apiKey)
        if (eventorOrganisation == null || eventorOrganisation.organisationId.content != organisationId) {
            return false
        }

        organisation.apiKey = apiKey
        organisationRepository.save(organisation)
        return true
    }
}


