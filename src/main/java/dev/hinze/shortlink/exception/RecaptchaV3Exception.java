package dev.hinze.shortlink.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RecaptchaV3Exception extends RuntimeException {

    public RecaptchaV3Exception(String message) {
        super(message);
    }

}
