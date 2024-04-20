package no.stunor.origo.eventorapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import no.stunor.origo.eventorapi.api.EventorService;
import no.stunor.origo.eventorapi.api.exception.EventorApiKeyException;
import no.stunor.origo.eventorapi.api.exception.EventorConnectionException;
import no.stunor.origo.eventorapi.data.EventorRepository;
import no.stunor.origo.eventorapi.data.OrganisationRepository;
import no.stunor.origo.eventorapi.model.Eventor;
import no.stunor.origo.eventorapi.model.organisation.Organisation;

@Service
public class OrganisationService {

    @Autowired
    EventorRepository eventorRepository;
    @Autowired
    OrganisationRepository organisationRepository;
    @Autowired
    EventorService eventorService;
    

    public Boolean validateApiKey(String eventorId, String organisationId) {
        Eventor eventor = eventorRepository.findByEventorId(eventorId).block();
        Organisation organisation = organisationRepository.findByOrganisationIdAndEventorId(organisationId, eventorId).block();
        if(organisation.getApiKey() == null) {
            return false;
        }
        try{
            org.iof.eventor.Organisation eventorOrganisation = eventorService.getOrganisatonFromApiKey(eventor.getBaseUrl(), organisation.getApiKey());
            if(eventorOrganisation == null || eventorOrganisation.getOrganisationId() == null) {
                return false;
            }
            return organisation.getOrganisationId().equals(eventorOrganisation.getOrganisationId().getContent());
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 403) {
                return false;
            }
            throw new EventorConnectionException();
        }
    }

    public void updateApiKey(String eventorId, String organisationId, String apiKey) {
        Eventor eventor = eventorRepository.findByEventorId(eventorId).block();
        Organisation organisation = organisationRepository.findByOrganisationIdAndEventorId(organisationId, eventorId).block();
        
        try{
            org.iof.eventor.Organisation eventorOrganisation = eventorService.getOrganisatonFromApiKey(eventor.getBaseUrl(), apiKey);
            if(eventorOrganisation == null || !eventorOrganisation.getOrganisationId().getContent().equals(organisationId)) {
                throw new EventorApiKeyException( "API key is not valid for given organisation");
            }
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 403) {
                throw new EventorApiKeyException("ApiKey does not belong to any organisation.");
            }
            throw new EventorConnectionException();
        }
        organisation.setApiKey(apiKey);
        organisationRepository.save(organisation).block();
    }

}


