package com.mgps.communication.service;

import com.mgps.communication.entity.Notification;
import com.mgps.communication.repository.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InAppNotificationChannel implements NotificationChannel {

    private final NotificationRepository notificationRepository;

    public InAppNotificationChannel(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void send(NotificationMessage message) {
        Notification notification = new Notification(
            UUID.randomUUID(),
            message.schoolId(),
            message.recipientUserId(),
            message.title(),
            message.body(),
            message.category()
        );
        notificationRepository.save(notification);
    }
}
