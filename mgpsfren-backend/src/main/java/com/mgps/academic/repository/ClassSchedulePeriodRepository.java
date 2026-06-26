package com.mgps.academic.repository;

import com.mgps.academic.entity.ClassSchedulePeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassSchedulePeriodRepository extends JpaRepository<ClassSchedulePeriod, UUID> {
    List<ClassSchedulePeriod> findByClassNameAndAcademicSessionOrderByDisplayOrderAsc(String className, String academicSession);
    Optional<ClassSchedulePeriod> findByClassNameAndAcademicSessionAndPeriodName(String className, String academicSession, String periodName);
}
