package no.stunor.origo.eventorapi.api.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class EventNotFoundException : RuntimeException("Event is not found")
