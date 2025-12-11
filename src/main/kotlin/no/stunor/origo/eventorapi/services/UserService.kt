package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.data.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.UUID

@Service
open class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    open fun delete(userId: UUID) {
        userRepository.deleteById(id = userId)
    }

}
