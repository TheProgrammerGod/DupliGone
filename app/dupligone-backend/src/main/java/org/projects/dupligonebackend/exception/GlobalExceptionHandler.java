package org.projects.dupligonebackend.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingMatrixVariableException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<?> handleMissingHeader(MissingRequestHeaderException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error","Missing required header",
                        "details",exception.getHeaderName() + " is required"
                ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> handleEntityNotFound(EntityNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                   "error", "Resource not found",
                        "details", exception.getMessage()
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleInvalidUUID(IllegalArgumentException exception){
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                   "error", "Invalid input",
                        "details", exception.getMessage()
                ));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleInvalidImage(ConstraintViolationException exception){
        List<String> errors = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Validation Failed",
                        "details", errors
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGeneralError(Exception exception){
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                   "error", "Unexpected error",
                        "details", exception.getMessage()
                ));
    }



}
