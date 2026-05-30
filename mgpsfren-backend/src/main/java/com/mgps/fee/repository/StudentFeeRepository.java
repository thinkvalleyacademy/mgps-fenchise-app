package com.mgps.fee.repository;

import com.mgps.fee.entity.StudentFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StudentFeeRepository extends JpaRepository<StudentFee, UUID> {
    List<StudentFee> findBySchoolId(UUID schoolId);
    List<StudentFee> findByStudentId(UUID studentId);
}
