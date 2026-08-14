package com.mgps.communication.service;

/**
 * A delivery mechanism for a {@link NotificationMessage}.
 *
 * The only implementation shipped today is {@link InAppNotificationChannel}, which
 * writes to the recipient's in-app inbox — no external provider or credentials
 * required. To add Email, SMS, or push delivery later, implement this interface and
 * register it as a Spring bean: {@link NotificationService} picks up every
 * {@code NotificationChannel} bean automatically and fans every message out to all
 * of them.
 */
public interface NotificationChannel {
    void send(NotificationMessage message);
}
