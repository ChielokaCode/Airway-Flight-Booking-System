package org.chielokacode.airwaycc.airwaybackendcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PasswordsDontMatchException extends RuntimeException{
    public PasswordsDontMatchException(String message) {
        super(message);
    }
}
