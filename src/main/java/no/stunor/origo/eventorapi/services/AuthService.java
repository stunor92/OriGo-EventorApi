package no.stunor.origo.eventorapi.services;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.stunor.origo.eventorapi.api.EventorService;
import no.stunor.origo.eventorapi.api.exception.EntityNotFoundException;
import no.stunor.origo.eventorapi.api.exception.EventorApiException;
import no.stunor.origo.eventorapi.data.EventorRepository;
import no.stunor.origo.eventorapi.data.PersonRepository;
import no.stunor.origo.eventorapi.model.firestore.Eventor;
import no.stunor.origo.eventorapi.model.firestore.Person;
import no.stunor.origo.eventorapi.services.converter.PersonConverter;


@Slf4j
@Service
public class AuthService {

    @Autowired
    EventorRepository eventorRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    EventorService eventorService;

    public Person authenticate(String eventorId, String username, String password, String userId) throws EventorApiException, EntityNotFoundException, InterruptedException, ExecutionException {
        
        Eventor eventor = eventorRepository.findByEventorId(eventorId).block();

        log.info("Start authenticating user {} on {}.", username, eventor.getName());

        Person person = PersonConverter.convertPerson(eventorService.authenticatePerson(eventor, username, password), eventor);

        Person existingPerson = personRepository.findByPersonIdAndEventorId(person.getPersonId(), eventor.getEventorId()).block();

        if(existingPerson == null){
            person.getUsers().add(userId);
            personRepository.save(person).block();
        } else {    
            person.setId(existingPerson.getId());

            person.getUsers().addAll(existingPerson.getUsers());
           
            if(!person.getUsers().contains(userId)){
                person.getUsers().add(userId);
            }

            personRepository.save(person).block();

        }
        
        return person;
        
    }
}
