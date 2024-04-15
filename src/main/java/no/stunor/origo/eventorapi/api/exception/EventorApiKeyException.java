package no.stunor.origo.eventorapi.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class EventorApiKeyException extends RuntimeException {
    public EventorApiKeyException(String message) {
        super(message);
    }
}
