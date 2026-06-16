package com.mgps.enquiry.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.enquiry.dto.EnquiryDTO;
import com.mgps.enquiry.dto.EnquiryRequestDTO;
import com.mgps.enquiry.service.EnquiryService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing enquiries.
 * Public POST endpoint for submission.
 * Secured GET endpoint for super admins.
 */
@RestController
@RequestMapping("/enquiries")
public class EnquiryController {

    private final EnquiryService enquiryService;

    public EnquiryController(EnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    /**
     * Submit a new enquiry.
     * This endpoint is public.
     */
    @PostMapping
    public ResponseEntity<ApiResponse<EnquiryDTO>> submitEnquiry(@Valid @RequestBody EnquiryRequestDTO request) {
        EnquiryDTO enquiry = enquiryService.createEnquiry(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(enquiry, "Enquiry submitted successfully"));
    }

    /**
     * Get all enquiries.
     * Restricted to super admins.
     */
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Page<EnquiryDTO>>> getAllEnquiries(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<EnquiryDTO> enquiries = enquiryService.getAllEnquiries(pageable);
        
        return ResponseEntity.ok(ApiResponse.success(enquiries, "Enquiries retrieved successfully"));
    }
}
