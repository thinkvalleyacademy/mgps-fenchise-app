package com.mgps.user.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.user.dto.UserDtos.AuthResponse;
import com.mgps.user.dto.UserDtos.LoginRequest;
import com.mgps.user.dto.UserDtos.LogoutRequest;
import com.mgps.user.dto.UserDtos.RegisterUserRequest;
import com.mgps.user.dto.UserDtos.RefreshRequest;
import com.mgps.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody RegisterUserRequest request) {
        AuthResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(response, "User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refresh(@RequestBody RefreshRequest request) {
        AuthResponse response = userService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token refreshed successfully"));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<?>> logout(@RequestBody LogoutRequest request) {
        userService.logout(request);
        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }
}
