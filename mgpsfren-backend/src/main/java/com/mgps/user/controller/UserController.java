package com.mgps.user.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.user.dto.UserDtos.*;
import com.mgps.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('USER_VIEW_SELF')")
    public ResponseEntity<ApiResponse<?>> me() {
        UserProfile profile = userService.getCurrentProfile();
        return ResponseEntity.ok(ApiResponse.success(profile, "Current user profile retrieved successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_LIST')")
    public ResponseEntity<ApiResponse<?>> listUsers(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size,
                                                    @RequestParam(required = false) UUID schoolId) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserProfile> users = schoolId != null
            ? userService.getUsersBySchool(schoolId, pageable)
            : userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponse.success(users, "Users retrieved successfully"));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_READ')")
    public ResponseEntity<ApiResponse<?>> getUser(@PathVariable UUID userId) {
        UserProfile profile = userService.getUserProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(profile, "User retrieved successfully"));
    }

    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasAuthority('USER_UPDATE_STATUS')")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable UUID userId,
                                                       @RequestBody UserStatusRequest request) {
        UserProfile profile = userService.updateStatus(userId, request);
        return ResponseEntity.ok(ApiResponse.success(profile, "User status updated successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<ApiResponse<?>> registerUser(@RequestBody RegisterUserRequest request) {
        AuthResponse response = userService.registerUser(request);
        return ResponseEntity.ok(ApiResponse.success(response.getProfile(), "User registered successfully"));
    }

    @PostMapping("/bulk-import")
    @PreAuthorize("hasAuthority('USER_BULK_IMPORT')")
    public ResponseEntity<ApiResponse<?>> bulkImport(@RequestParam("file") MultipartFile file,
                                                     @RequestParam(required = false) UUID schoolId) {
        BulkImportResult result = userService.bulkImportUsers(file, schoolId);
        return ResponseEntity.ok(ApiResponse.success(result, "Bulk user import completed"));
    }
}
