package no.stunor.origo.eventorapi.services

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import no.stunor.origo.eventorapi.data.UserRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class UserServiceTest {
    private lateinit var userRepository: UserRepository
    private lateinit var userService: UserService

    @BeforeEach
    fun setup() {
        userRepository = mockk()
        
        userService = UserService()
        
        // Use reflection to inject mock
        UserService::class.java.getDeclaredField("userRepository").apply {
            isAccessible = true
            set(userService, userRepository)
        }
    }

    @Test
    fun `delete should call repository deleteById with correct userId`() {
        // Given
        val userId = UUID.randomUUID()
        
        every { userRepository.deleteById(userId) } returns Unit

        // When
        userService.delete(userId)

        // Then
        verify { userRepository.deleteById(userId) }
    }
}
