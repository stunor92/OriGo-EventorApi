package no.stunor.origo.eventorapi.controller

import jakarta.servlet.http.HttpServletRequest
import no.stunor.origo.eventorapi.services.UserService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("user")
internal class UserController {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var userService: UserService

    @DeleteMapping()
    fun HttpServletRequest.delete() {
        val uid = getAttribute("uid") as String
        log.info("Start deleting user.")

        userService.delete(userId = uid)
    }
}
