package no.stunor.origo.eventorapi.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class EventorAuthException extends RuntimeException {
    public EventorAuthException() {
        super("Eventor authentication failed. Please check your credentials and try again.");
    }
}
