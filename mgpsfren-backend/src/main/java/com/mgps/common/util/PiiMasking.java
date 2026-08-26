package com.mgps.common.util;

/**
 * Small helpers to keep confidential values out of logs/audit rows while
 * leaving enough shape behind to be useful for debugging.
 */
public final class PiiMasking {

    private PiiMasking() {
    }

    /**
     * {@code mukesh.it15@gmail.com} -> {@code m***@gmail.com}. Keeps the domain
     * visible (useful for tracing tenant/domain-level issues) while hiding the
     * local part.
     */
    public static String maskEmail(String email) {
        if (email == null || email.isBlank()) {
            return email;
        }
        int at = email.indexOf('@');
        if (at <= 0) {
            return "***";
        }
        return email.charAt(0) + "***" + email.substring(at);
    }
}
