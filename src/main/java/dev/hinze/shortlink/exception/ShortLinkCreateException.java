package dev.hinze.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ShortLinkCreateException extends ResponseStatusException {

    public ShortLinkCreateException(String message) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

}
