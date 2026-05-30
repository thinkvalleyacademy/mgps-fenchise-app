package com.mgps.fee.repository;

import com.mgps.fee.entity.FeePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeePaymentRepository extends JpaRepository<FeePayment, UUID> {
    List<FeePayment> findBySchoolId(UUID schoolId);
    List<FeePayment> findByStudentFeeId(UUID studentFeeId);
}
