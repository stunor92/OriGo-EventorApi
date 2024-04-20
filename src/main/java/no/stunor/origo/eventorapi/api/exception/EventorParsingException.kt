package no.stunor.origo.eventorapi.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class EventorParsingException extends RuntimeException{

    public EventorParsingException() {
        super("Problem parsing eventor data.");
    }

}
