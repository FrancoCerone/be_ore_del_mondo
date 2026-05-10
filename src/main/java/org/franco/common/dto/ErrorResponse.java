package org.franco.common.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ErrorResponse(
        String code,
        String message,
        String path,
        OffsetDateTime timestamp,
        List<FieldError> errors) {

    public record FieldError(String field, String message) {
    }

    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(code, message, path, OffsetDateTime.now(), List.of());
    }
}
