package no.stunor.origo.eventorapi.controller

import jakarta.servlet.http.HttpServletRequest
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.services.PersonService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("person")
internal class PersonController {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var personService: PersonService

    @PostMapping("/{eventorId}")
    fun HttpServletRequest.authenticate(
        @PathVariable(value = "eventorId") eventorId: String,
        @RequestHeader(value = "username") username: String,
        @RequestHeader(value = "password") password: String
    ): ResponseEntity<Person> {
        log.info("Start authenticating user {}.", username)
        val uid = getAttribute("uid") as String

        return ResponseEntity(
            personService.authenticate(
                eventorId = eventorId,
                username = username,
                password = password,
                userId = uid
            ), HttpStatus.OK
        )
    }

    @DeleteMapping("/{eventorId}/{personId}")
    fun HttpServletRequest.delete(
        @PathVariable(value = "eventorId") eventorId: String,
        @PathVariable(value = "personId") personId: String
    ) {
        val uid = getAttribute("uid") as String
        log.info("Start deleting person.")

        personService.delete(
            eventorId = eventorId,
            personId = personId,
            userId = uid
        )
    }
}
