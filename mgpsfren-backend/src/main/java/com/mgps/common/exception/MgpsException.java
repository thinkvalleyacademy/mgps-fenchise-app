package com.mgps.common.exception;

/**
 * Base exception for MGPS application
 */
public class MgpsException extends RuntimeException {
    
    private String errorCode;
    
    public MgpsException(String message) {
        super(message);
        this.errorCode = "INTERNAL_ERROR";
    }
    
    public MgpsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public MgpsException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
