package com.mgps.communication.service;

import com.mgps.common.exception.BusinessLogicException;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.communication.dto.CommunicationDtos.AnnouncementRequest;
import com.mgps.communication.dto.CommunicationDtos.AnnouncementResponse;
import com.mgps.communication.entity.Announcement;
import com.mgps.communication.repository.AnnouncementRepository;
import com.mgps.tenant.TenantGuard;
import com.mgps.user.entity.AppUser;
import com.mgps.user.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AppUserRepository appUserRepository;
    private final NotificationService notificationService;
    private final TenantGuard tenantGuard;

    public AnnouncementService(AnnouncementRepository announcementRepository, AppUserRepository appUserRepository,
                               NotificationService notificationService) {
        this(announcementRepository, appUserRepository, notificationService, null);
    }

    @Autowired
    public AnnouncementService(AnnouncementRepository announcementRepository, AppUserRepository appUserRepository,
                               NotificationService notificationService, TenantGuard tenantGuard) {
        this.announcementRepository = announcementRepository;
        this.appUserRepository = appUserRepository;
        this.notificationService = notificationService;
        this.tenantGuard = tenantGuard != null ? tenantGuard : new TenantGuard();
    }

    public AnnouncementResponse createAnnouncement(AnnouncementRequest request, UUID createdBy) {
        if (request.getSchoolId() == null || request.getTitle() == null || request.getBody() == null) {
            throw new BusinessLogicException("schoolId, title and body are required");
        }
        tenantGuard.assertSchoolAccessible(request.getSchoolId());

        Announcement announcement = new Announcement(
            UUID.randomUUID(),
            request.getSchoolId(),
            request.getTitle(),
            request.getBody(),
            request.getAudienceRole(),
            request.getClassId(),
            createdBy,
            request.getExpiresAt()
        );
        Announcement saved = announcementRepository.save(announcement);

        fanOutNotifications(saved);

        return map(saved);
    }

    public List<AnnouncementResponse> getAnnouncements(UUID schoolId) {
        tenantGuard.assertSchoolAccessible(schoolId);
        LocalDateTime now = LocalDateTime.now();
        return announcementRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId).stream()
            .filter(a -> a.getExpiresAt() == null || a.getExpiresAt().isAfter(now))
            .map(this::map)
            .collect(Collectors.toList());
    }

    public void deleteAnnouncement(UUID announcementId) {
        Announcement announcement = getEntity(announcementId);
        tenantGuard.assertSchoolAccessible(announcement.getSchoolId());
        announcementRepository.deleteById(announcementId);
    }

    private Announcement getEntity(UUID announcementId) {
        return announcementRepository.findById(announcementId)
            .orElseThrow(() -> new ResourceNotFoundException("Announcement not found"));
    }

    /**
     * Every announcement drops a copy into the in-app inbox of each matching
     * recipient — school-wide when {@code audienceRole} is null, role-scoped
     * otherwise. Class-scoped targeting is not resolved to individual recipients
     * here since {@code AppUser} carries no class membership; {@code classId} is
     * kept on the announcement itself for the frontend to filter by.
     */
    private void fanOutNotifications(Announcement announcement) {
        List<AppUser> recipients = announcement.getAudienceRole() != null
            ? appUserRepository.findBySchoolIdAndRole(announcement.getSchoolId(), announcement.getAudienceRole())
            : appUserRepository.findBySchoolId(announcement.getSchoolId());

        for (AppUser recipient : recipients) {
            if (recipient.getId().equals(announcement.getCreatedBy())) {
                continue;
            }
            notificationService.notify(new NotificationMessage(
                announcement.getSchoolId(),
                recipient.getId(),
                announcement.getTitle(),
                announcement.getBody(),
                "ANNOUNCEMENT"
            ));
        }
    }

    private AnnouncementResponse map(Announcement announcement) {
        AnnouncementResponse response = new AnnouncementResponse();
        response.setAnnouncementId(announcement.getId());
        response.setSchoolId(announcement.getSchoolId());
        response.setTitle(announcement.getTitle());
        response.setBody(announcement.getBody());
        response.setAudienceRole(announcement.getAudienceRole());
        response.setClassId(announcement.getClassId());
        response.setCreatedBy(announcement.getCreatedBy());
        response.setExpiresAt(announcement.getExpiresAt());
        response.setCreatedAt(announcement.getCreatedAt());
        response.setUpdatedAt(announcement.getUpdatedAt());
        return response;
    }
}
