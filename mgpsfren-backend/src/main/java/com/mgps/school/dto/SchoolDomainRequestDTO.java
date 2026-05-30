package com.mgps.school.dto;

/**
 * DTO for Domain Request.
 */
public class SchoolDomainRequestDTO {

    private String domainName;
    private Boolean isPrimary = false;

    public SchoolDomainRequestDTO() {
    }

    public SchoolDomainRequestDTO(String domainName, Boolean isPrimary) {
        this.domainName = domainName;
        this.isPrimary = isPrimary;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean primary) {
        isPrimary = primary;
    }
}
