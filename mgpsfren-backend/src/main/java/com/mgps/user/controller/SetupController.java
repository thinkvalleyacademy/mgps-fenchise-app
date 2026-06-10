package com.mgps.user.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.common.exception.BusinessLogicException;
import com.mgps.user.dto.UserDtos.AuthResponse;
import com.mgps.user.dto.UserDtos.RegisterUserRequest;
import com.mgps.user.entity.UserRole;
import com.mgps.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/setup")
public class SetupController {

    private final UserService userService;

    public SetupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/superadmin")
    public ResponseEntity<ApiResponse<?>> superAdminStatus() {
        boolean exists = userService.hasSuperAdmin();
        return ResponseEntity.ok(ApiResponse.success(Collections.singletonMap("hasSuperAdmin", exists), "Super admin status retrieved successfully"));
    }

    @PostMapping("/superadmin")
    public ResponseEntity<ApiResponse<?>> bootstrapSuperAdmin(@RequestBody SuperAdminSetupRequest request) {
        if (userService.hasSuperAdmin()) {
            throw new BusinessLogicException("A Super Admin is already configured for this instance");
        }

        RegisterUserRequest register = new RegisterUserRequest();
        register.setSchoolId(null);
        register.setFirstName(request.getFirstName());
        register.setLastName(request.getLastName());
        register.setEmail(request.getEmail());
        register.setPhone(request.getPhone());
        register.setPassword(request.getPassword());
        register.setRole(UserRole.SUPER_ADMIN);

        AuthResponse response = userService.registerUser(register);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "Super admin created successfully"));
    }

    public static class SuperAdminSetupRequest {
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String password;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
