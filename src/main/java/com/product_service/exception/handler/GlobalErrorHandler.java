package com.product_service.exception.handler;

import com.product_service.exception.CustomMessageException;
import com.product_service.exception.ProductNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@Hidden
@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ProblemDetail> handleProductNotFoundException(ProductNotFoundException exception) {
        log.warn("Product not found: {}", exception.getMessage(), exception);
        ProblemDetail response = buildProblemDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.warn("Validation failed: {}", exception.getMessage(), exception);
        String errorMessage = exception.getBindingResult().getFieldErrors().stream()
                .map(error ->
                        String.format("Field '%s': %s", error.getField(), error.getDefaultMessage())
                )
                .collect(Collectors.joining(". "));

        ProblemDetail response = buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                errorMessage
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ProblemDetail> handlePropertyReferenceException(PropertyReferenceException exception) {
        log.warn("Invalid property reference in request: {}", exception.getMessage(), exception);
        ProblemDetail response = buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(CustomMessageException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ProblemDetail> handleCustomMessageException(CustomMessageException exception) {
        log.error("CustomMessageException occurred: {}", exception.getMessage(), exception);
        ProblemDetail response = buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<ProblemDetail> handleIllegalStateException(IllegalStateException exception) {
        log.error("Illegal state error occurred: {}", exception.getMessage(), exception);
        ProblemDetail response = buildProblemDetail(
                HttpStatus.UNPROCESSABLE_ENTITY,
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ProblemDetail> handleIllegalArgumentException(IllegalArgumentException exception) {
        log.error("Illegal argument error occured: {}", exception.getMessage(), exception);
        ProblemDetail response = buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private ProblemDetail buildProblemDetail(HttpStatus status, String message) {
        return ProblemDetail.forStatusAndDetail(status, message);
    }
}