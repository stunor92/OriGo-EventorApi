package no.stunor.origo.eventorapi.services;

import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import io.grpc.netty.shaded.io.netty.handler.codec.http.HttpStatusClass;
import no.stunor.origo.eventorapi.api.EventorService;
import no.stunor.origo.eventorapi.api.exception.EntityNotFoundException;
import no.stunor.origo.eventorapi.api.exception.EventorApiException;
import no.stunor.origo.eventorapi.data.EventorRepository;
import no.stunor.origo.eventorapi.data.OrganisationRepository;
import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Organisation;

@Service
public class OrganisationService {

    @Autowired
    EventorRepository eventorRepository;
    @Autowired
    OrganisationRepository organisationRepository;
    @Autowired
    EventorService eventorService;
    

    public Boolean validateApiKey(String eventorId, String organisationId) throws EntityNotFoundException, EventorApiException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
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
        } catch (EventorApiException e) {
            return false;
        }
    }

    public void updateApiKey(String eventorId, String organisationId, String apiKey) throws EntityNotFoundException, EventorApiException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
        Eventor eventor = eventorRepository.findByEventorId(eventorId).block();
        Organisation organisation = organisationRepository.findByOrganisationIdAndEventorId(organisationId, eventorId).block();
        org.iof.eventor.Organisation eventorOrganisation = eventorService.getOrganisatonFromApiKey(eventor.getBaseUrl(), organisation.getApiKey());
        if(eventorOrganisation == null || !eventorOrganisation.getOrganisationId().getContent().equals(organisationId)) {
            throw new EventorApiException(HttpStatusCode.valueOf(403), "API key is not valid for organisation");
        }
        organisation.setApiKey(apiKey);
        organisationRepository.save(organisation);
    }

}


