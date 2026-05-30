package com.mgps.school.exception;

import com.mgps.common.exception.MgpsException;

/**
 * Exception thrown when database provisioning fails.
 */
public class DatabaseProvisioningException extends MgpsException {
    public DatabaseProvisioningException(String message) {
        super("DATABASE_PROVISIONING_FAILED", message);
    }

    public DatabaseProvisioningException(String message, Throwable cause) {
        super("DATABASE_PROVISIONING_FAILED", message, cause);
    }
}
