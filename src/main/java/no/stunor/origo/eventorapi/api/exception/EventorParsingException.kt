package no.stunor.origo.eventorapi.api.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
class EventorParsingException : RuntimeException("Problem parsing eventor data.")
