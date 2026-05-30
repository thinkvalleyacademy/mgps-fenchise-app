package com.mgps.user.service;

import com.mgps.user.entity.AppUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;
    private final long refreshExpirationMs;

    public JwtService(@Value("${app.jwt.secret-key}") String secretKey,
                      @Value("${app.jwt.expiration}") long expirationMs,
                      @Value("${app.jwt.refresh-expiration}") long refreshExpirationMs) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String generateAccessToken(AppUser user) {
        return generateToken(user, "access", expirationMs);
    }

    public String generateRefreshToken(AppUser user) {
        return generateToken(user, "refresh", refreshExpirationMs);
    }

    public String generateToken(AppUser user, String tokenType, long expiresInMs) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("jti", UUID.randomUUID().toString());
        claims.put("userId", user.getId().toString());
        claims.put("schoolId", user.getSchoolId() != null ? user.getSchoolId().toString() : null);
        claims.put("role", user.getRole() != null ? user.getRole().name() : null);
        claims.put("status", user.getStatus() != null ? user.getStatus().name() : null);
        claims.put("tokenType", tokenType);
        return Jwts.builder()
            .claims(claims)
            .subject(user.getEmail())
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusMillis(expiresInMs)))
            .signWith(secretKey)
            .compact();
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public UUID extractUserId(String token) {
        Object userId = parseClaims(token).get("userId");
        return userId != null ? UUID.fromString(userId.toString()) : null;
    }

    public String extractTokenId(String token) {
        Object jti = parseClaims(token).get("jti");
        return jti != null ? jti.toString() : null;
    }

    public Instant extractExpiration(String token) {
        Date expiration = parseClaims(token).getExpiration();
        return expiration != null ? expiration.toInstant() : null;
    }

    public String extractTokenType(String token) {
        Object tokenType = parseClaims(token).get("tokenType");
        return tokenType != null ? tokenType.toString() : null;
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration() == null || claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}
