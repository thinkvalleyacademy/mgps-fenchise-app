package com.mgps.communication.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.communication.dto.CommunicationDtos.AnnouncementRequest;
import com.mgps.communication.service.AnnouncementService;
import com.mgps.user.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('COMMUNICATION_MANAGE')")
    public ResponseEntity<ApiResponse<?>> create(@RequestBody AnnouncementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(announcementService.createAnnouncement(request, currentUserId()), "Announcement posted successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> list(@RequestParam UUID schoolId) {
        return ResponseEntity.ok(ApiResponse.success(announcementService.getAnnouncements(schoolId), "Announcements retrieved successfully"));
    }

    @DeleteMapping("/{announcementId}")
    @PreAuthorize("hasAuthority('COMMUNICATION_MANAGE')")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable UUID announcementId) {
        announcementService.deleteAnnouncement(announcementId);
        return ResponseEntity.ok(ApiResponse.success(null, "Announcement deleted successfully"));
    }

    private UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal.getUserId();
        }
        return null;
    }
}
