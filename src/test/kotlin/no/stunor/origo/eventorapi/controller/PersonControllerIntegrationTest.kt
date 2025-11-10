package no.stunor.origo.eventorapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import no.stunor.origo.eventorapi.services.PersonService
import no.stunor.origo.eventorapi.testdata.PersonFactory
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(
    controllers = [PersonController::class],
    excludeAutoConfiguration = []
)
class PersonControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var personService: PersonService
    
    @MockkBean(relaxed = true)
    private lateinit var jwtInterceptor: no.stunor.origo.eventorapi.interceptor.JwtInterceptor

    @Test
    fun `authenticate should call service authenticate method and return OK`() {
        // Given
        val eventorId = "NOR"
        val username = "testuser"
        val password = "testpass"
        val userId = "user123"
        val mockPerson = PersonFactory.createTestPerson()
        
        every { personService.authenticate(eventorId, username, password, userId) } returns mockPerson

        // When & Then
        mockMvc.perform(
            post("/person/$eventorId")
                .header("username", username)
                .header("password", password)
                .requestAttr("uid", userId)
        )
            .andExpect(status().isOk)
    }

    @Test
    fun `delete should call service delete method`() {
        // Given
        val eventorId = "NOR"
        val personId = "123"
        val userId = "user123"
        
        justRun { personService.delete(eventorId, personId, userId) }

        // When & Then
        mockMvc.perform(
            delete("/person/$eventorId/$personId")
                .requestAttr("uid", userId)
        )
            .andExpect(status().isOk)
    }
}
