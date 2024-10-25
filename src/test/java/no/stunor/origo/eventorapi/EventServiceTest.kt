package no.stunor.origo.eventorapi


class EventServiceTest {
  /*  private lateinit var eventService: EventService

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
        return Organisation(organisationId = "141", name = "O Club", type = OrganisationType.Club, country = "NOR")
    }

    private fun generateRegion(): Region {
        return Region(regionId = "8", eventorId = "NOR", name = "Oslo")
    }


    private fun generateEventor(): Eventor {
        return Eventor(eventorId = "NOR", name = "Norway", federation = "NOF", baseUrl = "eventor.no", apiKey = "123abc")
    }
    */
}

