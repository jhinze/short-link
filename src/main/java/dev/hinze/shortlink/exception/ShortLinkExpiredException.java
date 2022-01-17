package dev.hinze.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ShortLinkExpiredException extends ResponseStatusException {

    public ShortLinkExpiredException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
