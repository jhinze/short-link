package dev.hinze.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ShortLinkNotFoundException extends ResponseStatusException {

    public ShortLinkNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
