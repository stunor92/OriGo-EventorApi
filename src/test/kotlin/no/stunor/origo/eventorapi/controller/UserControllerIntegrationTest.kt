package no.stunor.origo.eventorapi.controller

import com.ninjasquad.springmockk.MockkBean
import io.mockk.justRun
import no.stunor.origo.eventorapi.services.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(
    controllers = [UserController::class],
    excludeAutoConfiguration = []
)
class UserControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var userService: UserService
    
    @MockkBean(relaxed = true)
    private lateinit var jwtInterceptor: no.stunor.origo.eventorapi.interceptor.JwtInterceptor

    @Test
    fun `delete should call service delete method`() {
        // Given
        val userId = "user123"
        
        justRun { userService.delete(userId) }

        // When & Then
        mockMvc.perform(
            delete("/user")
                .requestAttr("uid", userId)
        )
            .andExpect(status().isOk)
    }
}
