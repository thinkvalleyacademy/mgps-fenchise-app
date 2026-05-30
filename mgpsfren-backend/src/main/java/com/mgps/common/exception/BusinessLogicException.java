package com.mgps.common.exception;

/**
 * Exception thrown for invalid operations or business logic violations
 */
public class BusinessLogicException extends MgpsException {
    
    public BusinessLogicException(String message) {
        super("BUSINESS_LOGIC_ERROR", message);
    }
    
    public BusinessLogicException(String errorCode, String message) {
        super(errorCode, message);
    }
}
