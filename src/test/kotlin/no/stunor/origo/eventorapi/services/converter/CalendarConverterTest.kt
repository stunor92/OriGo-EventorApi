package no.stunor.origo.eventorapi.services.converter

import no.stunor.origo.eventorapi.data.OrganisationRepository
import no.stunor.origo.eventorapi.data.RegionRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

class CalendarConverterTest {
    private val organisationRepository: OrganisationRepository = mock()
    private val regionRepository: RegionRepository = mock()
    private val converter = CalendarConverter(organisationRepository, regionRepository)

    private fun invokeConvertTimeSec(input: String?): Int {
        val m = CalendarConverter::class.java.getDeclaredMethod("convertTimeSec", String::class.java)
        m.isAccessible = true
        return m.invoke(converter, input) as Int
    }

    @Test
    fun `convertTimeSec parses HHmmss`() {
        assertEquals(3723, invokeConvertTimeSec("01:02:03")) // 1*3600 + 2*60 + 3
    }

    @Test
    fun `convertTimeSec parses mmss fallback`() {
        assertEquals(135, invokeConvertTimeSec("02:15")) // 2*60 + 15
    }

    @Test
    fun `convertTimeSec handles null`() {
        assertEquals(0, invokeConvertTimeSec(null))
    }

    @Test
    fun `convertTimeSec zero time`() {
        assertEquals(0, invokeConvertTimeSec("00:00:00"))
    }
}

