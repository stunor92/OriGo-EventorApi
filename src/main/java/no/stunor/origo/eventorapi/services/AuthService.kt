package no.stunor.origo.eventorapi.services

import no.stunor.origo.eventorapi.api.EventorService
import no.stunor.origo.eventorapi.api.exception.EventorAuthException
import no.stunor.origo.eventorapi.api.exception.EventorConnectionException
import no.stunor.origo.eventorapi.data.EventorRepository
import no.stunor.origo.eventorapi.data.PersonRepository
import no.stunor.origo.eventorapi.model.person.Person
import no.stunor.origo.eventorapi.services.converter.PersonConverter
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException

@Service
class AuthService {
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var eventorRepository: EventorRepository

    @Autowired
    private lateinit var personRepository: PersonRepository

    @Autowired
    private lateinit var eventorService: EventorService

    @Autowired
    private lateinit var personConverter: PersonConverter

    fun authenticate(eventorId: String, username: String, password: String, userId: String): Person {
        try {
            val eventor = eventorRepository.findByEventorId(eventorId).block()!!

            log.info("Start authenticating user {} on {}.", username, eventor.name)

            val eventorPerson = eventorService.authenticatePerson(eventor, username, password)?: throw EventorAuthException()

            val person = personConverter.convertPerson(eventorPerson, eventor)

            val existingPerson = personRepository.findByPersonIdAndEventorId(person.personId, eventor.eventorId).block()

            if (existingPerson == null) {
                person.users.add(userId)
                personRepository.save(person).block()
            } else {
                person.id = existingPerson.id

                person.users = existingPerson.users

                if (!person.users.contains(userId)) {
                    person.users.add(userId)
                }

                personRepository.save(person).block()
            }
            return person
        } catch (e: HttpClientErrorException) {
            if (e.statusCode.value() == 401) {
                throw EventorAuthException()
            }
            throw EventorConnectionException()
        }
    }
}
