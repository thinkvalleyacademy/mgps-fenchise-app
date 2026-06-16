package com.mgps.enquiry.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnquiryRequestDTO {
    
    @NotBlank(message = "Full name is required")
    @Size(max = 255)
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255)
    private String email;
    
    @NotBlank(message = "Mobile number is required")
    @Size(max = 20)
    private String mobileNumber;
    
    @Size(max = 50)
    private String studentClass;
    
    private String query;
}
