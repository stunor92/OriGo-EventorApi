package no.stunor.origo.eventorapi.services;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.stunor.origo.eventorapi.api.EventorService;
import no.stunor.origo.eventorapi.controller.exception.InvalidInputException;
import no.stunor.origo.eventorapi.data.EventorRepository;
import no.stunor.origo.eventorapi.model.Eventor;
import no.stunor.origo.eventorapi.model.event.Event;
import no.stunor.origo.eventorapi.services.converter.EventConverter;

@Slf4j
@Service
public class OrganiserService {

    @Autowired
    EventorRepository eventorRepository;
    @Autowired
    EventorService eventorService;
    @Autowired
    EventConverter eventConverter;



    public  List<Event>  listEvents(String eventorId, String organisationId) {

        Eventor eventor = eventorRepository.findByEventorId(eventorId).block();
        if(eventor == null){
            throw new InvalidInputException("Eventor not found");
        }

        LocalDate fromDate = LocalDate.now().minusMonths(1);
        LocalDate toDate = LocalDate.now().plusMonths(1);

        return eventConverter.convertEvents(eventorService.getEventList(eventor, fromDate, toDate, List.of(organisationId), null), eventor);
    }
}