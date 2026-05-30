package com.mgps.user.service;

import com.mgps.user.entity.AppUser;
import com.mgps.user.entity.UserRole;
import com.mgps.user.entity.UserStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void shouldGenerateAndValidateToken() {
        JwtService jwtService = new JwtService(
            "0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
            86400000L,
            604800000L
        );
        AppUser user = AppUser.builder()
            .id(UUID.randomUUID())
            .email("admin@example.com")
            .role(UserRole.SUPER_ADMIN)
            .status(UserStatus.ACTIVE)
            .build();

        String token = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        assertThat(token).isNotBlank();
        assertThat(refreshToken).isNotBlank();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractEmail(token)).isEqualTo("admin@example.com");
        assertThat(jwtService.extractUserId(token)).isEqualTo(user.getId());
        assertThat(jwtService.extractTokenType(token)).isEqualTo("access");
        assertThat(jwtService.extractTokenType(refreshToken)).isEqualTo("refresh");
    }
}
