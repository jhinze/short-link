package dev.hinze.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RecaptchaV3Exception extends ResponseStatusException {

    public RecaptchaV3Exception(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }

}
