package no.stunor.origo.eventorapi.controller

import com.ninjasquad.springmockk.MockkBean
import no.stunor.origo.eventorapi.services.UserService
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@Disabled("MockMvc not available in Spring Boot 4.0.0 - needs refactoring")
@SpringBootTest
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @MockkBean
    private lateinit var userService: UserService
    
    @MockkBean(relaxed = true)
    private lateinit var jwtInterceptor: no.stunor.origo.eventorapi.interceptor.JwtInterceptor

    @Test
    fun `delete should call service delete method`() {
        // TODO: Refactor to use RestClient or TestRestTemplate instead of MockMvc
        /*
        val userId = "user123"
        
        justRun { userService.delete(userId) }
        */
    }
}
