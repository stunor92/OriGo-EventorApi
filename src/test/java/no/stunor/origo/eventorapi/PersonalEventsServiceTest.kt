package no.stunor.origo.eventorapi

import jakarta.xml.bind.JAXBContext
import jakarta.xml.bind.JAXBException
import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.PersonRepository
import no.stunor.origo.eventorapi.model.Eventor
import no.stunor.origo.eventorapi.model.person.Gender
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.model.person.PersonName
import no.stunor.origo.eventorapi.services.UserEntryService
import org.iof.eventor.EntryList
import org.iof.eventor.ResultListList
import org.iof.eventor.StartListList
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.File
import java.text.ParseException
import java.util.concurrent.ExecutionException

@SpringBootTest
class PersonalEventsServiceTest {
    @Autowired
    private lateinit var userEntryService: UserEntryService

    @MockBean
    private lateinit var eventorService: EventorService

    @MockBean
    private lateinit var personRepository: PersonRepository

    @MockBean
    private lateinit var eventorRepository: EventorRepository

    @BeforeEach
    fun setUp() {
        whenever(personRepository.findAllByUsersContains(any())).thenReturn(Flux.fromArray(arrayOf<Person?>(generatePerson())))
        whenever(eventorRepository.findByEventorId(any())).thenReturn(Mono.just(generateEventor()))
    }

    @Test
    @Throws(JAXBException::class, InterruptedException::class, ExecutionException::class, NumberFormatException::class, ParseException::class)
    fun testPersonResultInactive() {
        whenever(eventorService.getGetOrganisationEntries(any(), any(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/resultInactive/OrganisationEntries.xml"))
        whenever(eventorService.getGetPersonalResults(any(), any(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/resultInactive/PersonalResult.xml"))
        whenever(eventorService.getGetPersonalStarts(any(), any(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/resultInactive/PersonalStart.xml"))


        userEntryService.userRaces("abc", generateEventor(), "123")
    }


    @Test
    @Throws(JAXBException::class, InterruptedException::class, ExecutionException::class, NumberFormatException::class, ParseException::class)
    fun testPersonNotSignedUp() {
        whenever(eventorService.getGetOrganisationEntries(any(), any(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/notSignedUp/OrganisationEntries.xml"))
        whenever(eventorService.getGetPersonalResults(any(), any(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/notSignedUp/PersonalResult.xml"))
        whenever(eventorService.getGetPersonalStarts(any(), any(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/notSignedUp/PersonalStart.xml"))
        
        userEntryService.userRaces("abc", generateEventor(), "123")
    }

    /* 
   @Test                                                                                          
    public void testPersonSignedUp() throws EntityNotFoundException, EventorApiException, JAXBException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
        when(eventorService.getGetOrganisationEntries(any(Eventor.class), anyList(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/signedUp/OrganisationEntries.xml"));
        when(eventorService.getGetPersonalResults(any(Eventor.class), anyString(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/signedUp/PersonalResult.xml"));
        when(eventorService.getGetPersonalStarts(any(Eventor.class), anyString(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/signedUp/PersonalStart.xml"));

        userEntryService.userRaces("abc", generateEventor(), "123");
    }
*/
    @Test
    @Throws(JAXBException::class, NumberFormatException::class)
    fun testPersonStartTime() {
        whenever(eventorService.getGetOrganisationEntries(any(), any(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/personStartTime/OrganisationEntries.xml"))
        whenever(eventorService.getGetPersonalResults(any(), any(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/personStartTime/PersonalResult.xml"))
        whenever(eventorService.getGetPersonalStarts(any(), any(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/personStartTime/PersonalStart.xml"))

        userEntryService.userRaces("abc", generateEventor(), "123")
    }


    @Throws(JAXBException::class)
    private fun generateEntryListFromXml(path: String): EntryList {
        val file = File(path)
        val jaxbContext = JAXBContext.newInstance(EntryList::class.java)

        val jaxbUnmarshalled = jaxbContext.createUnmarshaller()
        return jaxbUnmarshalled.unmarshal(file) as EntryList
    }

    @Throws(JAXBException::class)
    private fun generateResultListListFromXml(path: String): ResultListList {
        val file = File(path)
        val jaxbContext = JAXBContext.newInstance(ResultListList::class.java)

        val jaxbUnmarshalled = jaxbContext.createUnmarshaller()
        return jaxbUnmarshalled.unmarshal(file) as ResultListList
    }

    @Throws(JAXBException::class)
    private fun generateStartListListFromXml(path: String): StartListList {
        val file = File(path)
        val jaxbContext = JAXBContext.newInstance(StartListList::class.java)

        val jaxbUnmarshalled = jaxbContext.createUnmarshaller()
        return jaxbUnmarshalled.unmarshal(file) as StartListList
    }

    private fun generatePerson(): Person {
        val name = PersonName("Olsen", "Peter")

        val owner: MutableList<String> = ArrayList()
        owner.add("123")

        return Person(id = null, eventorId = "NOR", personId = "123", name =  name, birthYear = 1900, nationality = "NOR", gender = Gender.MAN, users = owner, mobilePhone = "12345678", email = "a@b.no", memberships = HashMap())
    }

    private fun generateEventor(): Eventor {
        return Eventor(eventorId = "NOR", name = "Norway", federation = "NOF", baseUrl = "eventor.no", apiKey = "123abc")
    }
}
