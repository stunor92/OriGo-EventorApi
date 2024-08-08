package no.stunor.origo.eventorapi

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBException
import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.data.RegionRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.Region
import no.stunor.origo.eventorapi.model.organisation.Organisation
import no.stunor.origo.eventorapi.model.organisation.OrganisationType
import no.stunor.origo.eventorapi.services.EventService
import org.iof.eventor.DocumentList
import org.iof.eventor.Event
import org.iof.eventor.EventClassList
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.io.File
import java.text.ParseException
import java.util.concurrent.ExecutionException

@SpringBootTest
class EventServiceTest {
    @Autowired
    private lateinit var eventService: EventService

    @MockBean
    private lateinit var eventorService: EventorService

    @MockBean
    private lateinit var organisationRepository: OrganisationRepository

    @MockBean
    private lateinit var regionRepository: RegionRepository

    @MockBean
    private lateinit var eventorRepository: EventorRepository

    @BeforeEach
    fun setUp() {
        whenever(organisationRepository.findByOrganisationIdAndEventorId(any(), any())).thenReturn(generateOrganisation())
        whenever(regionRepository.findByRegionIdAndEventorId(any(), any())).thenReturn(generateRegion())
        whenever(eventorRepository.findByEventorId(any())).thenReturn(generateEventor())
    }

    @Test
    @Throws(JAXBException::class, InterruptedException::class, ExecutionException::class, NumberFormatException::class, ParseException::class)
    fun testSingleRaceEvent() {
        whenever(eventorService.getEvent(any(), any(), any())).thenReturn(generateEventFromXml("src/test/resources/eventorResponse/eventService/oneDayEvent/Event.xml"))
        whenever(eventorService.getEventClasses(any(), any())).thenReturn(generateEventClassListFromXml("src/test/resources/eventorResponse/eventService/oneDayEvent/EventClassList.xml"))
        whenever(eventorService.getEventDocuments(any(), any(), any())).thenReturn(generateDocumentListFromXml("src/test/resources/eventorResponse/eventService/oneDayEvent/DocumentList.xml"))
        eventService.getEvent(eventorId = "NOR", eventId= "17535")
    }

    @Test
    @Throws(JAXBException::class, InterruptedException::class, ExecutionException::class, NumberFormatException::class, ParseException::class)
    fun testMultiRaceEvent() {
        whenever(eventorService.getEvent(any(), any(), any())).thenReturn(generateEventFromXml("src/test/resources/eventorResponse/eventService/multiDaysEvent/Event.xml"))
        whenever(eventorService.getEventClasses(any(), any())).thenReturn(generateEventClassListFromXml("src/test/resources/eventorResponse/eventService/multiDaysEvent/EventClassList.xml"))
        whenever(eventorService.getEventDocuments(any(), any(), any())).thenReturn(generateDocumentListFromXml("src/test/resources/eventorResponse/eventService/multiDaysEvent/DocumentList.xml"))

        eventService.getEvent(eventorId = "NOR", eventId = "18527")
    }


    @Test
    @Throws(JAXBException::class, InterruptedException::class, ExecutionException::class, NumberFormatException::class, ParseException::class)
    fun testRelay() {
        whenever(eventorService.getEvent(any(), any(), any())).thenReturn(generateEventFromXml("src/test/resources/eventorResponse/eventService/relayEvent/Event.xml"))
        whenever(eventorService.getEventClasses(any(), any())).thenReturn(generateEventClassListFromXml("src/test/resources/eventorResponse/eventService/relayEvent/EventClassList.xml"))
        whenever(eventorService.getEventDocuments(any(), any(), any())).thenReturn(generateDocumentListFromXml("src/test/resources/eventorResponse/eventService/relayEvent/DocumentList.xml"))

        eventService.getEvent(eventorId = "NOR", eventId = "17469")
    }

    @Throws(JAXBException::class)
    private fun generateEventFromXml(path: String): Event {
        val file = File(path)
        val jaxbContext = JAXBContext.newInstance(Event::class.java)

        val jaxbUnmarshalled = jaxbContext.createUnmarshaller()
        return jaxbUnmarshalled.unmarshal(file) as Event
    }

    @Throws(JAXBException::class)
    private fun generateEventClassListFromXml(path: String): EventClassList {
        val file = File(path)
        val jaxbContext = JAXBContext.newInstance(EventClassList::class.java)

        val jaxbUnmarshalled = jaxbContext.createUnmarshaller()
        return jaxbUnmarshalled.unmarshal(file) as EventClassList
    }

    @Throws(JAXBException::class)
    private fun generateDocumentListFromXml(path: String): DocumentList {
        val file = File(path)
        val jaxbContext = JAXBContext.newInstance(DocumentList::class.java)

        val jaxbUnmarshalled = jaxbContext.createUnmarshaller()
        return jaxbUnmarshalled.unmarshal(file) as DocumentList
    }

    private fun generateOrganisation(): Organisation {
        return Organisation(organisationId = "141", eventorId = "NOR", name = "O Club", type = OrganisationType.Club, country = "NOR", email = "mail@club.org", apiKey = null, regionId = null, contactPerson = null)
    }

    private fun generateRegion(): Region {
        return Region(regionId = "8", eventorId = "NOR", name = "Oslo")
    }


    private fun generateEventor(): Eventor {
        return Eventor(eventorId = "NOR", name = "Norway", federation = "NOF", baseUrl = "eventor.no", apiKey = "123abc")
    }
}

