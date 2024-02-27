package no.stunor.origo.eventorapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import no.stunor.origo.eventorapi.repository.EventorRepository;

@SpringBootTest
public class EventorRepositoryTest {

    @Autowired
    EventorRepository eventorRepository;

    @Test
    public void testGetEventorNorge(){
        assertEquals("Eventor Norge", eventorRepository.findByEventorId("NOR").blockFirst().getName());
    }
}
