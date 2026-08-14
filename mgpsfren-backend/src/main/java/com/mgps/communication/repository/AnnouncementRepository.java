package com.mgps.communication.repository;

import com.mgps.communication.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AnnouncementRepository extends JpaRepository<Announcement, UUID> {
    List<Announcement> findBySchoolIdOrderByCreatedAtDesc(UUID schoolId);
}
