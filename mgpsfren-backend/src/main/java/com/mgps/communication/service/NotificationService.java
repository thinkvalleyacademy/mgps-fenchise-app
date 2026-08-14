package com.mgps.communication.service;

import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.communication.dto.CommunicationDtos.NotificationResponse;
import com.mgps.communication.entity.Notification;
import com.mgps.communication.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final List<NotificationChannel> channels;

    public NotificationService(NotificationRepository notificationRepository, List<NotificationChannel> channels) {
        this.notificationRepository = notificationRepository;
        this.channels = channels;
    }

    public void notify(NotificationMessage message) {
        for (NotificationChannel channel : channels) {
            try {
                channel.send(message);
            } catch (Exception ex) {
                log.error("Notification channel {} failed to deliver to {}", channel.getClass().getSimpleName(),
                    message.recipientUserId(), ex);
            }
        }
    }

    public List<NotificationResponse> getInboxForUser(UUID userId) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId).stream()
            .map(this::map)
            .collect(Collectors.toList());
    }

    public NotificationResponse markRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getRecipientUserId().equals(userId)) {
            throw new AccessDeniedException("Not permitted to modify another user's notification");
        }
        notification.setRead(true);
        return map(notificationRepository.save(notification));
    }

    private NotificationResponse map(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setNotificationId(notification.getId());
        response.setTitle(notification.getTitle());
        response.setBody(notification.getBody());
        response.setCategory(notification.getCategory());
        response.setRead(notification.isRead());
        response.setCreatedAt(notification.getCreatedAt());
        return response;
    }
}
