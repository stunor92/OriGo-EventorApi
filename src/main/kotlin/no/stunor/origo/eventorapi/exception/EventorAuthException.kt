package no.stunor.origo.eventorapi.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class EventorAuthException : RuntimeException(
    "Eventor authentication failed. Please check your credentials and try again."
)
