package com.mgps.enquiry.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryDTO {
    private UUID id;
    private String fullName;
    private String email;
    private String mobileNumber;
    private String studentClass;
    private String query;
    private LocalDateTime createdAt;
}
