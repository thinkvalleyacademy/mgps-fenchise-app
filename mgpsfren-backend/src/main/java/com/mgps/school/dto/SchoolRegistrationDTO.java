package com.mgps.school.dto;

import java.util.UUID;

/**
 * DTO for School Registration Request.
 */
public class SchoolRegistrationDTO {

    private String name;
    private String adminEmail;
    private String adminPhone;
    private String address;
    private String city;
    private String state;
    private String postalCode;
    private UUID subscriptionPlanId;

    public SchoolRegistrationDTO() {
    }

    public SchoolRegistrationDTO(String name, String adminEmail, String adminPhone, String address,
                                 String city, String state, String postalCode, UUID subscriptionPlanId) {
        this.name = name;
        this.adminEmail = adminEmail;
        this.adminPhone = adminPhone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public UUID getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(UUID subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String name;
        private String adminEmail;
        private String adminPhone;
        private String address;
        private String city;
        private String state;
        private String postalCode;
        private UUID subscriptionPlanId;

        private Builder() {
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder adminEmail(String adminEmail) {
            this.adminEmail = adminEmail;
            return this;
        }

        public Builder adminPhone(String adminPhone) {
            this.adminPhone = adminPhone;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder state(String state) {
            this.state = state;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder subscriptionPlanId(UUID subscriptionPlanId) {
            this.subscriptionPlanId = subscriptionPlanId;
            return this;
        }

        public SchoolRegistrationDTO build() {
            return new SchoolRegistrationDTO(name, adminEmail, adminPhone, address, city, state, postalCode, subscriptionPlanId);
        }
    }
}
