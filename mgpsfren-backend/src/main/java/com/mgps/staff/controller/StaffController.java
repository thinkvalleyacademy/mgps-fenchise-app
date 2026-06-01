package com.mgps.staff.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.staff.dto.StaffDtos.*;
import com.mgps.staff.service.StaffService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> onboard(@RequestBody StaffAdmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(staffService.onboardStaff(request), "Staff onboarded successfully"));
    }

    @GetMapping("/{staffId}")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getStaff(@PathVariable UUID staffId) {
        return ResponseEntity.ok(ApiResponse.success(staffService.getStaff(staffId), "Staff retrieved successfully"));
    }

    @PutMapping("/{staffId}")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> updateStaff(@PathVariable UUID staffId,
                                                      @RequestBody StaffUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(staffService.updateStaff(staffId, request), "Staff updated successfully"));
    }

    @DeleteMapping("/{staffId}")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> deleteStaff(@PathVariable UUID staffId) {
        staffService.deleteStaff(staffId);
        return ResponseEntity.ok(ApiResponse.success(null, "Staff member deleted successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> listStaff(@RequestParam UUID schoolId,
                                                    @RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StaffResponse> staff = staffService.getStaffBySchool(schoolId, pageable);
        return ResponseEntity.ok(ApiResponse.success(staff, "Staff retrieved successfully"));
    }

    @GetMapping("/department")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> listByDepartment(@RequestParam UUID schoolId,
                                                           @RequestParam UUID departmentId) {
        return ResponseEntity.ok(ApiResponse.success(staffService.getStaffByDepartment(schoolId, departmentId), "Department staff retrieved successfully"));
    }

    @PatchMapping("/{staffId}/department")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> assignDepartment(@PathVariable UUID staffId,
                                                           @RequestBody StaffDepartmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(staffService.assignDepartment(staffId, request), "Department assigned successfully"));
    }

    @PostMapping("/{staffId}/attendance")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> markAttendance(@PathVariable UUID staffId,
                                                         @RequestBody StaffAttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(staffService.markAttendance(staffId, request), "Attendance marked successfully"));
    }

    @GetMapping("/{staffId}/attendance/summary")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> attendanceSummary(@PathVariable UUID staffId) {
        return ResponseEntity.ok(ApiResponse.success(staffService.getAttendanceSummary(staffId), "Attendance summary retrieved successfully"));
    }

    @PostMapping("/{staffId}/leave")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> applyLeave(@PathVariable UUID staffId,
                                                     @RequestBody StaffLeaveRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(staffService.applyLeave(staffId, request), "Leave application created successfully"));
    }

    @GetMapping("/{staffId}/leave")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getLeaves(@PathVariable UUID staffId) {
        return ResponseEntity.ok(ApiResponse.success(staffService.getLeaves(staffId), "Leave applications retrieved successfully"));
    }

    @PatchMapping("/leave/{leaveId}/approve")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> approveLeave(@PathVariable UUID leaveId,
                                                       @RequestBody StaffLeaveDecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(staffService.approveLeave(leaveId, request), "Leave approved successfully"));
    }

    @PatchMapping("/leave/{leaveId}/reject")
    @PreAuthorize("hasAuthority('STAFF_MANAGE')")
    public ResponseEntity<ApiResponse<?>> rejectLeave(@PathVariable UUID leaveId,
                                                      @RequestBody StaffLeaveDecisionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(staffService.rejectLeave(leaveId, request), "Leave rejected successfully"));
    }
}
