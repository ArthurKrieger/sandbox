package dev.arthur.sandbox.shared.presentation.web;

import dev.arthur.sandbox.shared.domain.InvalidInputException;
import dev.arthur.sandbox.shared.domain.NotFoundException;
import dev.arthur.sandbox.shared.domain.UnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    ProblemDetail notFound(@NonNull NotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(InvalidInputException.class)
    ProblemDetail invalidInput(@NonNull InvalidInputException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(UnavailableException.class)
    ProblemDetail unavailable(@NonNull UnavailableException e) {
        log.warn("dependency unavailable", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    // The cause stays in the log; the response must not leak internals.
    @ExceptionHandler(Exception.class)
    ProblemDetail unexpected(@NonNull Exception e) {
        log.error("unhandled request failure", e);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "unexpected error");
    }
}
