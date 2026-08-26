package com.mgps.fee.repository;

import com.mgps.fee.entity.FeeSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeeSettingsRepository extends JpaRepository<FeeSettings, UUID> {
    Optional<FeeSettings> findBySchoolId(UUID schoolId);
}
