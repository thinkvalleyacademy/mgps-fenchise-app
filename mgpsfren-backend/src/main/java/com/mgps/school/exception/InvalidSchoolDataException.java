package com.mgps.school.exception;

import com.mgps.common.exception.MgpsException;

/**
 * Exception thrown when school data is invalid.
 */
public class InvalidSchoolDataException extends MgpsException {
    public InvalidSchoolDataException(String message) {
        super("INVALID_SCHOOL_DATA", message);
    }
}
