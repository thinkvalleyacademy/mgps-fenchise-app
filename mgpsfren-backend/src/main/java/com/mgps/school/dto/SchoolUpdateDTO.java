package com.mgps.school.dto;

/**
 * DTO for School Update Request.
 */
public class SchoolUpdateDTO {

    private String name;
    private String adminPhone;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String logoUrl;

    public SchoolUpdateDTO() {
    }

    public SchoolUpdateDTO(String name, String adminPhone, String address, String city, String state,
                           String postalCode, String logoUrl) {
        this.name = name;
        this.adminPhone = adminPhone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.logoUrl = logoUrl;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAdminPhone() { return adminPhone; }
    public void setAdminPhone(String adminPhone) { this.adminPhone = adminPhone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getLogoUrl() { return logoUrl; }
    public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String name;
        private String adminPhone;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private String logoUrl;

        private Builder() {
        }

        public Builder name(String name) { this.name = name; return this; }
        public Builder adminPhone(String adminPhone) { this.adminPhone = adminPhone; return this; }
        public Builder address(String address) { this.address = address; return this; }
        public Builder city(String city) { this.city = city; return this; }
        public Builder state(String state) { this.state = state; return this; }
        public Builder postalCode(String postalCode) { this.postalCode = postalCode; return this; }
        public Builder logoUrl(String logoUrl) { this.logoUrl = logoUrl; return this; }

        public SchoolUpdateDTO build() {
            return new SchoolUpdateDTO(name, adminPhone, address, city, state, postalCode, logoUrl);
        }
    }
}
