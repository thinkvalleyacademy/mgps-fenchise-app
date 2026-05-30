package com.mgps.school.exception;

import com.mgps.common.exception.MgpsException;

/**
 * Exception thrown when school already exists.
 */
public class SchoolAlreadyExistsException extends MgpsException {
    public SchoolAlreadyExistsException(String message) {
        super("SCHOOL_ALREADY_EXISTS", message);
    }
}
