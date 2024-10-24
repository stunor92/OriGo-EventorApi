package no.stunor.origo.eventorapi.controller


import jakarta.servlet.http.HttpServletRequest
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.services.AuthService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("person")
internal class PersonController {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var authService: AuthService

    @GetMapping("/download")
    fun HttpServletRequest.authenticate(
        @RequestHeader(value = "eventorId") eventorId: String,
        @RequestHeader(value = "username") username: String,
        @RequestHeader(value = "password") password: String
    ): ResponseEntity<Person> {
        log.info("Start authenticating user {}.", username)
        val uid = getAttribute("uid") as String
        return ResponseEntity(
            authService.authenticate(
                eventorId = eventorId,
                username = username,
                password = password,
                userId = uid
            ), HttpStatus.OK
        )
    }
}
