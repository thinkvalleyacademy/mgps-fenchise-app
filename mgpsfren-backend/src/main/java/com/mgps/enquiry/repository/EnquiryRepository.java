package com.mgps.enquiry.repository;

import com.mgps.enquiry.entity.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for Enquiry entity.
 */
@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry, UUID> {
}
