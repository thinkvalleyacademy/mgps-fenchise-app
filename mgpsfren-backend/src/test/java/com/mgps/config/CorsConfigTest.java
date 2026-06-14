package com.mgps.config;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    @Test
    void shouldUseSpecificDefaultOriginsInsteadOfWildcard() throws IOException {
        String applicationYaml = Files.readString(Path.of("src/main/resources/application.yml"));
        String applicationDevYaml = Files.readString(Path.of("src/main/resources/application-dev.yml"));

        assertThat(applicationYaml)
            .contains("allowed-origins: ${APP_CORS_ALLOWED_ORIGINS:https://mgpsfren.thinkvalleysoftwares.in,http://100.101.103.63:6080}");
        assertThat(applicationDevYaml)
            .contains("allowed-origins: ${APP_CORS_ALLOWED_ORIGINS:https://mgpsfren.thinkvalleysoftwares.in,http://100.101.103.63:6080}");
        assertThat(applicationYaml)
            .doesNotContain("allowed-origins: ${APP_CORS_ALLOWED_ORIGINS:*}");
        assertThat(applicationDevYaml)
            .doesNotContain("allowed-origins: ${APP_CORS_ALLOWED_ORIGINS:*}");
    }

    @Test
    void shouldNormalizeTrailingSlashInConfiguredOrigins() {
        CorsConfig config = new CorsConfig();
        ReflectionTestUtils.setField(config, "allowedOrigins", "https://mgpsfren.thinkvalleysoftwares.in/,http://100.101.103.63:6080");

        CorsConfigurationSource source = config.corsConfigurationSource();

        CorsConfiguration corsConfiguration = ((UrlBasedCorsConfigurationSource) source)
            .getCorsConfigurations()
            .values()
            .iterator()
            .next();

        assertThat(corsConfiguration.getAllowedOriginPatterns())
            .containsExactly("https://mgpsfren.thinkvalleysoftwares.in", "http://100.101.103.63:6080");
        assertThat(corsConfiguration.checkOrigin("https://mgpsfren.thinkvalleysoftwares.in"))
            .isEqualTo("https://mgpsfren.thinkvalleysoftwares.in");
        assertThat(corsConfiguration.checkOrigin("http://100.101.103.63:6080"))
            .isEqualTo("http://100.101.103.63:6080");
    }

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
