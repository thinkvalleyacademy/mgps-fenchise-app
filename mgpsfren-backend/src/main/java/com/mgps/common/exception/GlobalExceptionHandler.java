package com.mgps.common.exception;

import com.mgps.common.dto.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Global Exception Handler for all REST endpoints
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Override to handle validation errors with custom format
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, 
            HttpHeaders headers, 
            HttpStatusCode status, 
            WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        
        log.warn("Validation error: {}", errors);
        return new ResponseEntity<>(
            ApiResponse.error("VALIDATION_ERROR", "Input validation failed", errors.toString()),
            HttpStatus.BAD_REQUEST
        );
    }
    
    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(
            ApiResponse.error(ex.getErrorCode(), ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }
    
    /**
     * Handle DuplicateResourceException
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateResource(
            DuplicateResourceException ex, WebRequest request) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return new ResponseEntity<>(
            ApiResponse.error(ex.getErrorCode(), ex.getMessage()),
            HttpStatus.CONFLICT
        );
    }
    
    /**
     * Handle BusinessLogicException
     */
    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<ApiResponse<?>> handleBusinessLogicException(
            BusinessLogicException ex, WebRequest request) {
        log.warn("Business logic error: {}", ex.getMessage());
        return new ResponseEntity<>(
            ApiResponse.error(ex.getErrorCode(), ex.getMessage()),
            HttpStatus.BAD_REQUEST
        );
    }
    
    /**
     * Handle MgpsException (base exception)
     */
    @ExceptionHandler(MgpsException.class)
    public ResponseEntity<ApiResponse<?>> handleMgpsException(
            MgpsException ex, WebRequest request) {
        log.error("MGPS Exception: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
            ApiResponse.error(ex.getErrorCode(), ex.getMessage()),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    /**
     * Handle AccessDeniedException
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDenied(
            AccessDeniedException ex, WebRequest request) {
        log.warn("Access denied: {}", ex.getMessage());
        return new ResponseEntity<>(
            ApiResponse.error("ACCESS_DENIED", "You don't have permission to access this resource"),
            HttpStatus.FORBIDDEN
        );
    }
    
    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(
            Exception ex, WebRequest request) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
            ApiResponse.error("INTERNAL_ERROR", "An unexpected error occurred"),
            HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
