package com.mgps.communication.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.communication.service.NotificationService;
import com.mgps.user.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Every endpoint here is self-scoped to the caller's own inbox — there is no
 * schoolId or userId request parameter, so there is nothing for a caller to
 * spoof to reach another user's notifications.
 */
@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> myInbox() {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getInboxForUser(currentUserId()), "Notifications retrieved successfully"));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<?>> markRead(@PathVariable UUID notificationId) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.markRead(notificationId, currentUserId()), "Notification marked as read"));
    }

    private UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal.getUserId();
        }
        throw new AccessDeniedException("Authentication required");
    }
}
