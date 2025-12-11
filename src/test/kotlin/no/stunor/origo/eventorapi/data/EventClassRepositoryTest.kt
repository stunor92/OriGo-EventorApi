package no.stunor.origo.eventorapi.data

import no.stunor.origo.eventorapi.model.event.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant
import java.util.*

@SpringBootTest
@ActiveProfiles("test")
@Transactional
open class EventClassRepositoryTest {

    @Autowired
    lateinit var eventClassRepository: EventClassRepository

    @Autowired
    lateinit var eventRepository: EventRepository

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    private lateinit var testEventId: UUID

    @BeforeEach
    fun setup() {
        // Create a test event to associate classes with
        val event = Event(
            eventorId = "TEST",
            eventorRef = "12345",
            name = "Test Event",
            type = EventFormEnum.Individual,
            classification = EventClassificationEnum.Club,
            status = EventStatusEnum.Created,
            startDate = Timestamp.from(Instant.now()),
            finishDate = Timestamp.from(Instant.now())
        )
        val savedEvent = eventRepository.save(event)
        testEventId = savedEvent.id!!
    }

    @Test
    fun `should save and find event class by event id`() {
        // Given
        val eventClass = EventClass(
            eventorRef = "H21",
            name = "H21 Elite",
            shortName = "H21E",
            type = EventClassTypeEnum.Normal,
            minAge = 20,
            maxAge = 100,
            gender = ClassGender.Men,
            presentTime = true,
            orderedResult = true,
            legs = 1,
            minAverageAge = 20,
            maxAverageAge = 100
        )

        // When
        val saved = eventClassRepository.save(eventClass, testEventId)

        // Then
        assertNotNull(saved.id)

        val found = eventClassRepository.findByEventId(testEventId)
        assertEquals(1, found.size)
        assertEquals("H21 Elite", found[0].name)
        assertEquals("H21E", found[0].shortName)
        assertEquals(EventClassTypeEnum.Normal, found[0].type)
        assertEquals(ClassGender.Men, found[0].gender)
    }

    @Test
    fun `should find multiple classes by event id`() {
        // Given
        val class1 = EventClass(
            eventorRef = "H21",
            name = "H21 Elite",
            shortName = "H21E",
            type = EventClassTypeEnum.Normal,
            minAge = 20,
            maxAge = 100,
            gender = ClassGender.Men
        )
        val class2 = EventClass(
            eventorRef = "D21",
            name = "D21 Elite",
            shortName = "D21E",
            type = EventClassTypeEnum.Normal,
            minAge = 20,
            maxAge = 100,
            gender = ClassGender.Women
        )
        val class3 = EventClass(
            eventorRef = "H35",
            name = "H35",
            shortName = "H35",
            type = EventClassTypeEnum.Normal,
            minAge = 35,
            maxAge = 100,
            gender = ClassGender.Men
        )

        // When
        eventClassRepository.save(class1, testEventId)
        eventClassRepository.save(class2, testEventId)
        eventClassRepository.save(class3, testEventId)

        // Then
        val found = eventClassRepository.findByEventId(testEventId)
        assertEquals(3, found.size)
        assertTrue(found.any { it.name == "H21 Elite" })
        assertTrue(found.any { it.name == "D21 Elite" })
        assertTrue(found.any { it.name == "H35" })
    }

    @Test
    fun `should find event class by id`() {
        // Given
        val eventClass = EventClass(
            eventorRef = "H21",
            name = "H21 Elite",
            shortName = "H21E",
            type = EventClassTypeEnum.Normal,
            minAge = 20,
            maxAge = 100,
            gender = ClassGender.Men
        )
        val saved = eventClassRepository.save(eventClass, testEventId)

        // When
        val found = eventClassRepository.findById(saved.id!!)

        // Then
        assertNotNull(found)
        assertEquals("H21 Elite", found?.name)
        assertEquals("H21E", found?.shortName)
    }

    @Test
    fun `should find event class by event id and eventor ref`() {
        // Given
        val eventClass = EventClass(
            eventorRef = "H21",
            name = "H21 Elite",
            shortName = "H21E",
            type = EventClassTypeEnum.Normal,
            minAge = 20,
            maxAge = 100,
            gender = ClassGender.Men
        )
        eventClassRepository.save(eventClass, testEventId)

        // When
        val found = eventClassRepository.findByEventIdAndEventorRef(testEventId, "H21")

        // Then
        assertNotNull(found)
        assertEquals("H21 Elite", found?.name)
        assertEquals("H21", found?.eventorRef)
    }

    @Test
    fun `should return null when event class not found by id`() {
        // When
        val found = eventClassRepository.findById(UUID.randomUUID())

        // Then
        assertNull(found)
    }

    @Test
    fun `should return empty list when no classes found for event`() {
        // When
        val found = eventClassRepository.findByEventId(UUID.randomUUID())

        // Then
        assertTrue(found.isEmpty())
    }

    @Test
    fun `should update existing event class on conflict`() {
        // Given
        val eventClass = EventClass(
            eventorRef = "H21",
            name = "H21 Elite",
            shortName = "H21E",
            type = EventClassTypeEnum.Normal,
            minAge = 20,
            maxAge = 100,
            gender = ClassGender.Men
        )
        eventClassRepository.save(eventClass, testEventId)

        // When - save again with same eventorRef but different data
        val updatedClass = EventClass(
            eventorRef = "H21",
            name = "H21 Elite Updated",
            shortName = "H21",
            type = EventClassTypeEnum.Normal,
            minAge = 21,
            maxAge = 99,
            gender = ClassGender.Men
        )
        eventClassRepository.save(updatedClass, testEventId)

        // Then
        val found = eventClassRepository.findByEventIdAndEventorRef(testEventId, "H21")
        assertNotNull(found)
        assertEquals("H21 Elite Updated", found?.name)
        assertEquals("H21", found?.shortName)
        assertEquals(21, found?.minAge)
        assertEquals(99, found?.maxAge)

        // Should still only have one class
        val allClasses = eventClassRepository.findByEventId(testEventId)
        assertEquals(1, allClasses.size)
    }

    @Test
    fun `should delete all classes for an event`() {
        // Given
        val class1 = EventClass(
            eventorRef = "H21",
            name = "H21 Elite",
            shortName = "H21E",
            type = EventClassTypeEnum.Normal,
            minAge = 20,
            maxAge = 100,
            gender = ClassGender.Men
        )
        val class2 = EventClass(
            eventorRef = "D21",
            name = "D21 Elite",
            shortName = "D21E",
            type = EventClassTypeEnum.Normal,
            minAge = 20,
            maxAge = 100,
            gender = ClassGender.Women
        )
        eventClassRepository.save(class1, testEventId)
        eventClassRepository.save(class2, testEventId)

        // Verify classes were saved
        assertEquals(2, eventClassRepository.findByEventId(testEventId).size)

        // When
        eventClassRepository.deleteByEventId(testEventId)

        // Then
        val found = eventClassRepository.findByEventId(testEventId)
        assertTrue(found.isEmpty())
    }

    @Test
    fun `should handle different class types`() {
        // Given
        val normalClass = EventClass(
            eventorRef = "H21",
            name = "H21",
            shortName = "H21",
            type = EventClassTypeEnum.Normal,
            minAge = 20,
            maxAge = 100,
            gender = ClassGender.Men
        )
        val eliteClass = EventClass(
            eventorRef = "H21E",
            name = "H21 Elite",
            shortName = "H21E",
            type = EventClassTypeEnum.Elite,
            minAge = 0,
            maxAge = 99,
            gender = ClassGender.Men
        )

        // When
        eventClassRepository.save(normalClass, testEventId)
        eventClassRepository.save(eliteClass, testEventId)

        // Then
        val found = eventClassRepository.findByEventId(testEventId)
        assertEquals(2, found.size)
        assertTrue(found.any { it.type == EventClassTypeEnum.Normal })
        assertTrue(found.any { it.type == EventClassTypeEnum.Elite })
    }

    @Test
    fun `should handle class with age restrictions`() {
        // Given
        val ageRestrictedClass = EventClass(
            eventorRef = "H35",
            name = "H35",
            shortName = "H35",
            type = EventClassTypeEnum.Normal,
            minAge = 35,
            maxAge = 100,
            gender = ClassGender.Men,
            legs = 1,
            minAverageAge = 35,
            maxAverageAge = 100
        )

        // When
        val saved = eventClassRepository.save(ageRestrictedClass, testEventId)

        // Then
        val found = eventClassRepository.findById(saved.id!!)
        assertNotNull(found)
        assertEquals(EventClassTypeEnum.Normal, found?.type)
        assertEquals(35, found?.minAge)
        assertEquals(100, found?.maxAge)
        assertEquals(35, found?.minAverageAge)
        assertEquals(100, found?.maxAverageAge)
    }
}

