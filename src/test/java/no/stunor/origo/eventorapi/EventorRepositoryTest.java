package no.stunor.origo.eventorapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import no.stunor.origo.eventorapi.repository.EventorRepository;
import no.stunor.origo.eventorapi.repository.PersonRepository;

@SpringBootTest
public class EventorRepositoryTest {

    @Autowired
    EventorRepository eventorRepository;

    @Autowired
    PersonRepository personRepository;

    @Test
    public void testGetEventorNorge(){
        assertEquals("Eventor Norge", eventorRepository.findByEventorId("NOR").block().getName());
    }
}
