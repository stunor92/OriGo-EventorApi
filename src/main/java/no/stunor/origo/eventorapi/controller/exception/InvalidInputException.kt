package no.stunor.origo.eventorapi.controller.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class InvalidInputException(message: String?) : RuntimeException(message)
