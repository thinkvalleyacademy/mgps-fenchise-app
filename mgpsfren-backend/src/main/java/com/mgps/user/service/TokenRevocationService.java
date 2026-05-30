package com.mgps.user.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory token revocation list for logout support.
 * Suitable for MVP development; can later be replaced with Redis or DB-backed storage.
 */
@Service
public class TokenRevocationService {

    private final Map<String, Instant> revokedTokens = new ConcurrentHashMap<>();

    public void revoke(String tokenId, Instant expiresAt) {
        if (tokenId != null && expiresAt != null) {
            revokedTokens.put(tokenId, expiresAt);
        }
    }

    public boolean isRevoked(String tokenId) {
        if (tokenId == null) {
            return false;
        }
        Instant expiresAt = revokedTokens.get(tokenId);
        return expiresAt != null && expiresAt.isAfter(Instant.now());
    }

    @Scheduled(fixedDelay = 300000)
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        revokedTokens.entrySet().removeIf(entry -> entry.getValue() == null || !entry.getValue().isAfter(now));
    }
}
