package no.stunor.origo.eventorapi.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
class StartListNotFoundException : RuntimeException(
    "Start-list is not found"
)
