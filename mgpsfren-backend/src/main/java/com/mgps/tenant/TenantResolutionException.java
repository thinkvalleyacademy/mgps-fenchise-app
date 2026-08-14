package com.mgps.tenant;

import com.mgps.common.exception.MgpsException;

/**
 * Raised when a request cannot be bound to a tenant safely.
 *
 * This is deliberately a hard failure: routing an ambiguous request to the
 * master datasource (the previous behaviour) silently exposed control-plane
 * data to tenant users, so tenant resolution now fails closed instead.
 */
public class TenantResolutionException extends MgpsException {

    public TenantResolutionException(String message) {
        super(message);
    }
}
