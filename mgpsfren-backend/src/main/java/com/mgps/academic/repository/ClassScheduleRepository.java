package com.mgps.academic.repository;

import com.mgps.academic.entity.ClassSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClassScheduleRepository extends JpaRepository<ClassSchedule, UUID> {
    List<ClassSchedule> findByClassNameAndAcademicSession(String className, String academicSession);
    void deleteByClassNameAndAcademicSession(String className, String academicSession);
}
