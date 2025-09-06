package no.stunor.origo.eventorapi.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(value = HttpStatus.NOT_IMPLEMENTED)
class EventNotSupportedException : RuntimeException("OriGo does not not support competitors-handling for relays or team-races yet.")
