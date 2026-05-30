package com.mgps.user.repository;

import com.mgps.user.entity.AppUser;
import com.mgps.user.entity.UserRole;
import com.mgps.user.entity.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByRole(UserRole role);
    Page<AppUser> findBySchoolId(UUID schoolId, Pageable pageable);
    Page<AppUser> findByStatus(UserStatus status, Pageable pageable);
    Page<AppUser> findByRole(UserRole role, Pageable pageable);
}
