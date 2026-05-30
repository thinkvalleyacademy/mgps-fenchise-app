package com.mgps.common.exception;

/**
 * Exception thrown when resource is not found
 */
public class ResourceNotFoundException extends MgpsException {
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super("NOT_FOUND", 
            String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }
    
    public ResourceNotFoundException(String message) {
        super("NOT_FOUND", message);
    }
}
