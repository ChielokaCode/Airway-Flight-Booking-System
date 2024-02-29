package org.chielokacode.airwaycc.airwaybackendcc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class FlightNotFoundException extends Exception{
    public FlightNotFoundException(String message) {
        super(message);
    }
}
