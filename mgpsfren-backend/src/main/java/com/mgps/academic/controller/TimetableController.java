package com.mgps.academic.controller;

import com.mgps.academic.dto.TimetableDtos.TimetableConflictResponse;
import com.mgps.academic.dto.TimetableDtos.TimetableRequest;
import com.mgps.academic.service.TimetableService;
import com.mgps.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/timetable")
public class TimetableController {

    private final TimetableService timetableService;

    public TimetableController(TimetableService timetableService) {
        this.timetableService = timetableService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> create(@RequestBody TimetableRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(timetableService.createTimetableEntry(request), "Timetable entry created successfully"));
    }

    @PostMapping("/conflicts")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> checkConflict(@RequestBody TimetableRequest request) {
        TimetableConflictResponse conflict = timetableService.checkConflict(request);
        return ResponseEntity.ok(ApiResponse.success(conflict, "Conflict check completed"));
    }

    @GetMapping("/class")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getClassTimetable(@RequestParam UUID schoolId,
                                                            @RequestParam UUID academicYearId,
                                                            @RequestParam UUID classId) {
        return ResponseEntity.ok(ApiResponse.success(
            timetableService.getTimetableForClass(schoolId, academicYearId, classId),
            "Class timetable retrieved successfully"));
    }

    @GetMapping("/day")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getDayTimetable(@RequestParam UUID schoolId,
                                                          @RequestParam UUID academicYearId,
                                                          @RequestParam String dayOfWeek) {
        return ResponseEntity.ok(ApiResponse.success(
            timetableService.getTimetableForDay(schoolId, academicYearId, dayOfWeek),
            "Daily schedule retrieved successfully"));
    }

    @GetMapping("/weekly")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getWeeklyTimetable(@RequestParam UUID schoolId,
                                                             @RequestParam UUID academicYearId) {
        return ResponseEntity.ok(ApiResponse.success(
            timetableService.getWeeklySchedule(schoolId, academicYearId),
            "Weekly schedule retrieved successfully"));
    }
}
