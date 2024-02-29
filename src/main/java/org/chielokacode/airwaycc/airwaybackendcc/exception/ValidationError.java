package org.chielokacode.airwaycc.airwaybackendcc.exception;

public record ValidationError(
        String field,
        String message
) {
}