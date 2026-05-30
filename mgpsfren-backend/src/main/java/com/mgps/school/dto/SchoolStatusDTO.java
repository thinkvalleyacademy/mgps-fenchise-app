package com.mgps.school.dto;

import com.mgps.school.entity.SchoolStatus;

/**
 * DTO for School Status Change Request.
 */
public class SchoolStatusDTO {

    private SchoolStatus status;

    public SchoolStatusDTO() {
    }

    public SchoolStatusDTO(SchoolStatus status) {
        this.status = status;
    }

    public SchoolStatus getStatus() {
        return status;
    }

    public void setStatus(SchoolStatus status) {
        this.status = status;
    }
}
