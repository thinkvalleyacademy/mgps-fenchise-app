package com.mgps.common.exception;

/**
 * Exception thrown for duplicate resource operations
 */
public class DuplicateResourceException extends MgpsException {
    
    public DuplicateResourceException(String resourceName, String fieldName, Object fieldValue) {
        super("DUPLICATE_RESOURCE", 
            String.format("%s already exists with %s: %s", resourceName, fieldName, fieldValue));
    }
    
    public DuplicateResourceException(String message) {
        super("DUPLICATE_RESOURCE", message);
    }
}
