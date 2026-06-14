package com.mgps.tenant;

import java.text.Normalizer;
import java.util.Locale;
import java.util.stream.Stream;

public final class TenantNamingUtil {

    public static final String CLIENT_TENANT_ID = "THINKVALLEY_ACADEMY_FREN";

    private TenantNamingUtil() {
    }

    public static String generateTenantId(String schoolName, String city, String postalCode) {
        String tenantId = Stream.of(
                normalizeTenantPart(schoolName),
                normalizeTenantPart(city),
                normalizeTenantPart(postalCode)
            )
            .filter(part -> part != null && !part.isBlank())
            .reduce((left, right) -> left + "_" + right)
            .orElse("");

        return tenantId.toUpperCase(Locale.ROOT);
    }

    public static String generateDatabaseName(String schoolName, String city, String postalCode) {
        String tenantId = generateTenantId(schoolName, city, postalCode);
        return tenantId.toLowerCase(Locale.ROOT);
    }

    private static String normalizeTenantPart(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "")
            .replaceAll("[^A-Za-z0-9]+", "_")
            .replaceAll("_+", "_")
            .replaceAll("^_+|_+$", "")
            .trim();

        return normalized.toUpperCase(Locale.ROOT);
    }
}
