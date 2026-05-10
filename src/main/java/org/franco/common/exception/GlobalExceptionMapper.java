package org.franco.common.exception;

import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.OffsetDateTime;
import org.franco.common.dto.ErrorResponse;
import org.jboss.logging.Logger;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionMapper.class);

    @Context
    UriInfo uriInfo;

    @Override
    public Response toResponse(Throwable exception) {
        if (exception instanceof ConstraintViolationException validationException) {
            var errors = validationException.getConstraintViolations().stream()
                    .map(violation -> new ErrorResponse.FieldError(violation.getPropertyPath().toString(), violation.getMessage()))
                    .toList();
            return build(Response.Status.BAD_REQUEST, "VALIDATION_ERROR", "Validation failed", errors);
        }
        if (exception instanceof ApiNotFoundException) {
            return build(Response.Status.NOT_FOUND, "NOT_FOUND", exception.getMessage(), java.util.List.of());
        }
        if (exception instanceof ConflictException) {
            return build(Response.Status.CONFLICT, "CONFLICT", exception.getMessage(), java.util.List.of());
        }
        if (exception instanceof UnauthorizedException) {
            return build(Response.Status.UNAUTHORIZED, "UNAUTHORIZED", exception.getMessage(), java.util.List.of());
        }
        if (exception instanceof WebApplicationException webException) {
            Response.Status status = Response.Status.fromStatusCode(webException.getResponse().getStatus());
            return build(status == null ? Response.Status.BAD_REQUEST : status, "REQUEST_ERROR", exception.getMessage(), java.util.List.of());
        }

        LOG.error("Unhandled API error", exception);
        return build(Response.Status.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Unexpected error", java.util.List.of());
    }

    private Response build(Response.Status status, String code, String message, java.util.List<ErrorResponse.FieldError> errors) {
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ErrorResponse(code, message, uriInfo == null ? null : uriInfo.getPath(), OffsetDateTime.now(), errors))
                .build();
    }
}
