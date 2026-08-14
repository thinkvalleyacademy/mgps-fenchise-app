package com.mgps.communication.service;

import java.util.UUID;

/**
 * A message to deliver to one recipient, independent of the {@link NotificationChannel}
 * that ends up carrying it.
 */
public record NotificationMessage(UUID schoolId, UUID recipientUserId, String title, String body, String category) {
}
