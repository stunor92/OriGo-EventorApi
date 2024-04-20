package no.stunor.origo.eventorapi.api.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
class EventorConnectionException : RuntimeException("We are currently not able to connect to Eventor. Please try again later.")
