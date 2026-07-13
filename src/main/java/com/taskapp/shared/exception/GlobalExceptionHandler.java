package com.taskapp.shared.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.jsonwebtoken.MalformedJwtException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleEntityNotFoundException(EntityNotFoundException ex, WebRequest request) {
        log.warn("Entity not found: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, "ENTITY_NOT_FOUND", ex.getMessage(), request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(ValidationException ex, WebRequest request) {
        log.warn("Validation failed: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_EXCEPTION", ex.getMessage(), request);
    }

    @ExceptionHandler(StateTransitionException.class)
    public ResponseEntity<ExceptionResponse> handleStateTransitionException(StateTransitionException ex, WebRequest request) {
        log.warn("Invalid state transition: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "STATE_TRANSITION_EXCEPTION", ex.getMessage(), request);
    }

    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<ExceptionResponse> handleDuplicateException(DuplicateException ex, WebRequest request) {
        log.warn("Duplicate record: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "DUPLICATE_EXCEPTION", ex.getMessage(), request);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ExceptionResponse> handleBusinessRuleViolationException(BusinessRuleViolationException ex, WebRequest request) {
        log.warn("Business rule violation: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "BUSINESS_RULE_VIOLATION", ex.getMessage(), request);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        log.warn("Failed login attempt: {}", ex.getMessage());
        return buildResponse(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS", "Invalid username or password. Please try again.", request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ExceptionResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied for user: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "ACCESS_DENIED", "You do not have permission to perform this action.", request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Invalid method arguments provided.");
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return buildResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", errors, request);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ExceptionResponse> handleMalformedJwtException(MalformedJwtException ex, WebRequest request) {
        log.warn("Invalid JWT token: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "INVALID_TOKEN", "Invalid token format.", request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex, WebRequest request) {
        log.error("An unexpected internal error occurred", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred. Please try again.", request);
    }

    private ResponseEntity<ExceptionResponse> buildResponse(HttpStatus status, String code, Object message, WebRequest request) {
        ExceptionResponse exResponse = new ExceptionResponse(
                status.value(),
                code,
                message,
                request.getDescription(false),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(exResponse);
    }
}
