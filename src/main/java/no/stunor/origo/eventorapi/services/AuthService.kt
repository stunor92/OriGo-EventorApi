package no.stunor.origo.eventorapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import lombok.extern.slf4j.Slf4j;
import no.stunor.origo.eventorapi.api.EventorService;
import no.stunor.origo.eventorapi.api.exception.EventorAuthException;
import no.stunor.origo.eventorapi.api.exception.EventorConnectionException;
import no.stunor.origo.eventorapi.data.EventorRepository;
import no.stunor.origo.eventorapi.data.PersonRepository;
import no.stunor.origo.eventorapi.model.Eventor;
import no.stunor.origo.eventorapi.model.person.Person;
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

    public Person authenticate(String eventorId, String username, String password, String userId) {
        try{
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
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode().value() == 401) {
                throw new EventorAuthException();
            }
            throw new EventorConnectionException();
        }
        
    }
}
