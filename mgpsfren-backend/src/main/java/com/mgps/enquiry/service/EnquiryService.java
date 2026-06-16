package com.mgps.enquiry.service;

import com.mgps.enquiry.dto.EnquiryDTO;
import com.mgps.enquiry.dto.EnquiryRequestDTO;
import com.mgps.enquiry.entity.Enquiry;
import com.mgps.enquiry.repository.EnquiryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EnquiryService {

    private final EnquiryRepository enquiryRepository;

    public EnquiryService(EnquiryRepository enquiryRepository) {
        this.enquiryRepository = enquiryRepository;
    }

    @Transactional
    public EnquiryDTO createEnquiry(EnquiryRequestDTO request) {
        Enquiry enquiry = Enquiry.builder()
                .id(UUID.randomUUID())
                .fullName(request.getFullName())
                .email(request.getEmail())
                .mobileNumber(request.getMobileNumber())
                .studentClass(request.getStudentClass())
                .query(request.getQuery())
                .build();

        Enquiry savedEnquiry = enquiryRepository.save(enquiry);
        return mapToDTO(savedEnquiry);
    }

    @Transactional(readOnly = true)
    public Page<EnquiryDTO> getAllEnquiries(Pageable pageable) {
        return enquiryRepository.findAll(pageable).map(this::mapToDTO);
    }

    private EnquiryDTO mapToDTO(Enquiry enquiry) {
        return EnquiryDTO.builder()
                .id(enquiry.getId())
                .fullName(enquiry.getFullName())
                .email(enquiry.getEmail())
                .mobileNumber(enquiry.getMobileNumber())
                .studentClass(enquiry.getStudentClass())
                .query(enquiry.getQuery())
                .createdAt(enquiry.getCreatedAt())
                .build();
    }
}
