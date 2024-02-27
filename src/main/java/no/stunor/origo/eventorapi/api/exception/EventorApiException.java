package no.stunor.origo.eventorapi.api.exception;

import org.springframework.http.HttpStatusCode;

public class EventorApiException extends Throwable {
    HttpStatusCode statusCode;
    String statusText;

    public EventorApiException(HttpStatusCode statusCode, String statusText) {
        this.statusCode = statusCode;
        this.statusText = statusText;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getStatusText() {
        return statusText;
    }
}
