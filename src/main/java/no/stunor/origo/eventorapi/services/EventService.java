package no.stunor.origo.eventorapi.services;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.iof.eventor.DocumentList;
import org.iof.eventor.EntryList;
import org.iof.eventor.EventClassList;
import org.iof.eventor.ResultList;
import org.iof.eventor.StartList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.stunor.origo.eventorapi.api.EventorService;
import no.stunor.origo.eventorapi.api.exception.EventorParsingException;
import no.stunor.origo.eventorapi.data.EventorRepository;
import no.stunor.origo.eventorapi.data.RegionRepository;
import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Organisation;
import no.stunor.origo.eventorapi.model.firestore.Region;
import no.stunor.origo.eventorapi.model.origo.entry.EventEntryList;
import no.stunor.origo.eventorapi.model.origo.event.Event;
import no.stunor.origo.eventorapi.model.origo.result.RaceResultList;
import no.stunor.origo.eventorapi.model.origo.start.RaceStartList;
import no.stunor.origo.eventorapi.model.origo.user.UserRace;
import no.stunor.origo.eventorapi.services.converter.EntryListConverter;
import no.stunor.origo.eventorapi.services.converter.EventConverter;
import no.stunor.origo.eventorapi.services.converter.OrganisationConverter;
import no.stunor.origo.eventorapi.services.converter.ResultListConverter;
import no.stunor.origo.eventorapi.services.converter.StartListConverter;

@Slf4j
@Service
public class EventService {

    @Autowired
    EventorRepository eventorRepository;
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    UserEntryService userEntryService;
    @Autowired
    EventorService eventorService;
    @Autowired
    EventConverter eventConverter;
    

    public Event getEvent(String eventorId, String eventNumber, String userId) {
        Eventor eventor = eventorRepository.findByEventorId(eventorId).block();

        org.iof.eventor.Event event = eventorService.getEvent(eventor.getBaseUrl(), eventor.getApiKey(), eventNumber);
        EventClassList eventClassList = eventorService.getEventClasses(eventor, eventNumber);
        DocumentList documentList = eventorService.getEventDocuments(eventor.getBaseUrl(), eventor.getApiKey(), eventNumber);
        
        List<Organisation> organisers = new ArrayList<>();
        List<Region> regions = new ArrayList<>();

        for(Object o : event.getOrganiser().getOrganisationIdOrOrganisation()){
            org.iof.eventor.Organisation org = (org.iof.eventor.Organisation) o;    
            organisers.add(OrganisationConverter.convertOrganisation(org, eventor));
            Region region = null;
            if(org.getParentOrganisation().getOrganisationId() != null){
                region  = regionRepository.findByOrganisationIdAndEventorId(org.getParentOrganisation().getOrganisationId().getContent(), eventorId).block();
            } if (region == null) {
                log.info("{} does not have a region. check if {} is a regon.", org.getName().getContent(), org.getName().getContent());

                try {
                    region = regionRepository.findByOrganisationIdAndEventorId(org.getOrganisationId().getContent(), eventorId).block(); 
                } catch (Exception e1){
                    log.info("Region {} does not exist.", org.getOrganisationId().getContent());
                }
            }
            boolean regionExist = false;

            for(Region r : regions){
                if(region != null && region.getOrganisationId().equals(r.getOrganisationId())){
                    regionExist = true;
                }
            }
            if(!regionExist && region != null){
                regions.add(region);
            }
        }

        List<UserRace> raceCompetitors = new ArrayList<>();
        if(userId != null){
           raceCompetitors = userEntryService.userRaces(userId, eventor, eventNumber);
        }
        return eventConverter.convertEvent(event, eventClassList, documentList, organisers, regions, eventor, raceCompetitors);
    }

    public EventEntryList getEntryList(String eventorId, String eventNumber) {
        Eventor eventor = eventorRepository.findByEventorId(eventorId).block();
        EntryList entryList = eventorService.getEventEntryList(eventor.getBaseUrl(), eventor.getApiKey(), eventNumber);
        return EntryListConverter.convertEventEntryList(entryList, eventor);
    }  

    public List<RaceStartList> getStartList(String eventorId, String eventNumber) {
        Eventor eventor = eventorRepository.findByEventorId(eventorId).block();
        StartList startList = eventorService.getEventStartList(eventor.getBaseUrl(), eventor.getApiKey(), eventNumber);
        return StartListConverter.convertEventStartList(startList, eventor);
    }  

    public List<RaceResultList> getResultList(String eventorId, String eventNumber) {
        Eventor eventor = eventorRepository.findByEventorId(eventorId).block();
        ResultList resultList = eventorService.getEventResultList(eventor.getBaseUrl(), eventor.getApiKey(), eventNumber);
        try {
            return ResultListConverter.convertEventResultList(resultList, eventor);
        } catch (NumberFormatException | ParseException e ) {
            log.warn(e.getMessage());
            throw new EventorParsingException();
        }
    }  
}