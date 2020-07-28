package dev.hinze.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ShortLinkCreateException extends RuntimeException {

    public ShortLinkCreateException(String message) {
        super(message);
    }

}
