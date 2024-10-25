package no.stunor.origo.eventorapi

//@SpringBootTest
class PersonalEventsServiceTest {
    /*@Autowired
    private lateinit var calendarService: CalendarService

    @MockBean
    private lateinit var eventorService: EventorService

    @MockBean
    private lateinit var personRepository: PersonRepository

    @MockBean
    private lateinit var eventorRepository: EventorRepository

    @BeforeEach
    fun setUp() {
        whenever(personRepository.findAllByUserId(any())).thenReturn(listOf(generatePerson()))
        whenever(eventorRepository.findByEventorId(any())).thenReturn(generateEventor())
    }

    @Test
    @Throws(JAXBException::class, InterruptedException::class, ExecutionException::class, NumberFormatException::class, ParseException::class)
    fun testPersonResultInactive() {
        whenever(eventorService.getGetOrganisationEntries(any(), any(), any(), any(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/resultInactive/OrganisationEntries.xml"))
        whenever(eventorService.getGetPersonalResults(any(), any(), any(), any(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/resultInactive/PersonalResult.xml"))
        whenever(eventorService.getGetPersonalStarts(any(), any(), any(), any(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/resultInactive/PersonalStart.xml"))


        calendarService.getEventList(userId = "abc")
    }


    @Test
    @Throws(JAXBException::class, InterruptedException::class, ExecutionException::class, NumberFormatException::class, ParseException::class)
    fun testPersonNotSignedUp() {
        whenever(eventorService.getGetOrganisationEntries(any(), any(), any(), any(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/notSignedUp/OrganisationEntries.xml"))
        whenever(eventorService.getGetPersonalResults(any(), any(), any(), any(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/notSignedUp/PersonalResult.xml"))
        whenever(eventorService.getGetPersonalStarts(any(), any(), any(), any(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/notSignedUp/PersonalStart.xml"))

        calendarService.getEventList(userId = "abc")
    }

    /* 
   @Test                                                                                          
    public void testPersonSignedUp() throws EntityNotFoundException, EventorApiException, JAXBException, InterruptedException, ExecutionException, NumberFormatException, ParseException {
        when(eventorService.getGetOrganisationEntries(any(Eventor.class), anyList(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/signedUp/OrganisationEntries.xml"));
        when(eventorService.getGetPersonalResults(any(Eventor.class), anyString(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/signedUp/PersonalResult.xml"));
        when(eventorService.getGetPersonalStarts(any(Eventor.class), anyString(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/signedUp/PersonalStart.xml"));

        calendarService.getEventList(userId = "abc")
    }
*/
    @Test
    @Throws(JAXBException::class, NumberFormatException::class)
    fun testPersonStartTime() {
        whenever(eventorService.getGetOrganisationEntries(any(), any(), any(), any(), any())).thenReturn(generateEntryListFromXml("src/test/resources/eventorResponse/personalEventsService/personStartTime/OrganisationEntries.xml"))
        whenever(eventorService.getGetPersonalResults(any(), any(), any(), any(), any())).thenReturn(generateResultListListFromXml("src/test/resources/eventorResponse/personalEventsService/personStartTime/PersonalResult.xml"))
        whenever(eventorService.getGetPersonalStarts(any(), any(), any(), any(), any())).thenReturn(generateStartListListFromXml("src/test/resources/eventorResponse/personalEventsService/personStartTime/PersonalStart.xml"))

        calendarService.getEventList(userId = "abc")
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

        return Person(id = null, eventorId = "NOR", personId = "123", name =  name, birthYear = 1900, nationality = "NOR", gender = Gender.Man, users = owner, mobilePhone = "12345678", email = "a@b.no", memberships = HashMap())
    }

    private fun generateEventor(): Eventor {
        return Eventor(eventorId = "NOR", name = "Norway", federation = "NOF", baseUrl = "eventor.no", apiKey = "123abc")
    }

     */
}
