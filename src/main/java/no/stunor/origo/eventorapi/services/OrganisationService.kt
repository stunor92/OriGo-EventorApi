package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.api.exception.EventorApiKeyException
import no.stunor.origo.eventorapi.api.exception.EventorNotFoundException
import no.stunor.origo.eventorapi.api.exception.OrganisationApiKeyException
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
        val eventor = eventorRepository.findByEventorId(eventorId).block() ?: throw EventorNotFoundException()
        val organisation = organisationRepository.findByOrganisationIdAndEventorId(organisationId, eventorId).block() ?: throw OrganisationNotFoundException()
        if (organisation.apiKey == null) {
            return false
        }

        val eventorOrganisation = eventorService.getOrganisationFromApiKey(eventor.baseUrl, organisation.apiKey)
        if (eventorOrganisation == null || eventorOrganisation.organisationId == null) {
            return false
        }
        return organisation.organisationId == eventorOrganisation.organisationId.content
    }

    fun updateApiKey(eventorId: String?, organisationId: String, apiKey: String?) {
        val eventor = eventorRepository.findByEventorId(eventorId).block() ?: throw EventorNotFoundException()
        val organisation = organisationRepository.findByOrganisationIdAndEventorId(organisationId, eventorId!!).block() ?: throw OrganisationNotFoundException()

        val eventorOrganisation = eventorService.getOrganisationFromApiKey(eventor.baseUrl, apiKey) ?: throw OrganisationApiKeyException()
        if (eventorOrganisation.organisationId.content != organisationId) {
            throw EventorApiKeyException("API key is not valid for given organisation")
        }
        organisation.apiKey = apiKey
        organisationRepository.save(organisation).block()
    }
}


