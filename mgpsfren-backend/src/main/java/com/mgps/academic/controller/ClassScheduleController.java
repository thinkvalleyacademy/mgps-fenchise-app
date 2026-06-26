package com.mgps.academic.controller;

import com.mgps.academic.dto.ClassScheduleDTO;
import com.mgps.academic.dto.ClassSchedulePeriodDTO;
import com.mgps.academic.dto.DuplicateScheduleRequest;
import com.mgps.academic.service.ClassScheduleService;
import com.mgps.common.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/academic/schedules")
public class ClassScheduleController {

    private final ClassScheduleService service;

    public ClassScheduleController(ClassScheduleService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClassScheduleDTO>>> getSchedules(
            @RequestParam String className,
            @RequestParam String session,
            @RequestParam(required = false) Integer weekNumber) {
        List<ClassScheduleDTO> schedules = service.getSchedules(className, session, weekNumber);
        return ResponseEntity.ok(ApiResponse.success(schedules, "Schedules retrieved successfully"));
    }

    @GetMapping("/periods")
    public ResponseEntity<ApiResponse<List<ClassSchedulePeriodDTO>>> getPeriods(
            @RequestParam String className,
            @RequestParam String session) {
        List<ClassSchedulePeriodDTO> periods = service.getPeriods(className, session);
        return ResponseEntity.ok(ApiResponse.success(periods, "Periods retrieved successfully"));
    }

    @PostMapping("/periods")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClassSchedulePeriodDTO>> createPeriod(@RequestBody ClassSchedulePeriodDTO dto) {
        ClassSchedulePeriodDTO period = service.createPeriod(dto);
        return ResponseEntity.ok(ApiResponse.success(period, "Period saved successfully"));
    }

    @DeleteMapping("/periods/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePeriod(@PathVariable UUID id) {
        service.deletePeriod(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Period deleted successfully"));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClassScheduleDTO>> createSchedule(@RequestBody ClassScheduleDTO dto) {
        ClassScheduleDTO created = service.createSchedule(dto);
        return ResponseEntity.ok(ApiResponse.success(created, "Schedule created successfully"));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<ClassScheduleDTO>> updateSchedule(
            @PathVariable UUID id,
            @RequestBody ClassScheduleDTO dto) {
        ClassScheduleDTO updated = service.updateSchedule(id, dto);
        return ResponseEntity.ok(ApiResponse.success(updated, "Schedule updated successfully"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(@PathVariable UUID id) {
        service.deleteSchedule(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Schedule deleted successfully"));
    }

    @PostMapping("/duplicate")
    @PreAuthorize("hasAnyRole('SCHOOL_ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> duplicateSchedule(@RequestBody DuplicateScheduleRequest request) {
        service.duplicateSchedule(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Schedule duplicated successfully"));
    }
}
