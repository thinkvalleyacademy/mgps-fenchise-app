package com.mgps.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    @Test
    void shouldUseWildcardOriginPatternsWhenOriginsAreConfiguredAsWildcard() {
        CorsConfig config = new CorsConfig();
        ReflectionTestUtils.setField(config, "allowedOrigins", "*");

        CorsConfigurationSource source = config.corsConfigurationSource();

        assertThat(source).isInstanceOf(UrlBasedCorsConfigurationSource.class);

        CorsConfiguration corsConfiguration = ((UrlBasedCorsConfigurationSource) source)
            .getCorsConfigurations()
            .values()
            .iterator()
            .next();

        assertThat(corsConfiguration).isNotNull();
        assertThat(corsConfiguration.getAllowedOriginPatterns()).contains("*");
        assertThat(corsConfiguration.getAllowCredentials()).isTrue();
    }
}
