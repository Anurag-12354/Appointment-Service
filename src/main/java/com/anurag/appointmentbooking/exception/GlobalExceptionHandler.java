package com.anurag.appointmentbooking.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(DuplicateResourceException.class)
        public ResponseEntity<ApiError> handleDuplicateResource(
                        DuplicateResourceException exception) {
                ApiError error = new ApiError(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                exception.getMessage(),
                                Map.of());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(error);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiError> handleValidation(
                        MethodArgumentNotValidException exception) {
                Map<String, String> validationErrors = new LinkedHashMap<>();

                exception.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> validationErrors.putIfAbsent(
                                                error.getField(),
                                                error.getDefaultMessage()));

                ApiError error = new ApiError(
                                LocalDateTime.now(),
                                HttpStatus.BAD_REQUEST.value(),
                                "Validation failed",
                                validationErrors);

                return ResponseEntity
                                .badRequest()
                                .body(error);
        }

        @ExceptionHandler(DataIntegrityViolationException.class)
        public ResponseEntity<ApiError> handleDatabaseConstraint(
                        DataIntegrityViolationException exception) {
                ApiError error = new ApiError(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                "A database constraint was violated",
                                Map.of());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(error);
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiError> handleResourceNotFound(
                        ResourceNotFoundException exception) {
                ApiError error = new ApiError(
                                LocalDateTime.now(),
                                HttpStatus.NOT_FOUND.value(),
                                exception.getMessage(),
                                Map.of());

                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(error);
        }

        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<ApiError> handleIllegalState(
                        IllegalStateException exception) {
                ApiError error = new ApiError(
                                LocalDateTime.now(),
                                HttpStatus.CONFLICT.value(),
                                exception.getMessage(),
                                Map.of());

                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(error);
        }

        @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ApiError> handleBadCredentials(
                        BadCredentialsException exception) {
                ApiError error = new ApiError(
                                LocalDateTime.now(),
                                HttpStatus.UNAUTHORIZED.value(),
                                "Invalid email or password",
                                Map.of());

                return ResponseEntity
                                .status(HttpStatus.UNAUTHORIZED)
                                .body(error);
        }
        @ExceptionHandler(AccessDeniedException.class)
public ResponseEntity<ApiError> handleAccessDenied(
        AccessDeniedException exception) {

    ApiError error = new ApiError(
            LocalDateTime.now(),
            HttpStatus.FORBIDDEN.value(),
            exception.getMessage(),
            Map.of());

    return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body(error);
}
}