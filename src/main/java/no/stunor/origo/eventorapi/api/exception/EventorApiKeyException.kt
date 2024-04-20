package no.stunor.origo.eventorapi.api.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.FORBIDDEN)
class EventorApiKeyException(message: String?) : RuntimeException(message)
