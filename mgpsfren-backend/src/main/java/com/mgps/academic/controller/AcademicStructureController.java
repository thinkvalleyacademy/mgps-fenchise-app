package com.mgps.academic.controller;

import com.mgps.academic.dto.AcademicDtos.*;
import com.mgps.academic.service.AcademicStructureService;
import com.mgps.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/academic")
public class AcademicStructureController {

    private final AcademicStructureService academicStructureService;

    public AcademicStructureController(AcademicStructureService academicStructureService) {
        this.academicStructureService = academicStructureService;
    }

    @PostMapping("/years")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> createAcademicYear(@RequestBody AcademicYearRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(academicStructureService.createAcademicYear(request), "Academic year created successfully"));
    }

    @GetMapping("/years")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getAcademicYears(@RequestParam UUID schoolId) {
        return ResponseEntity.ok(ApiResponse.success(academicStructureService.getAcademicYears(schoolId), "Academic years retrieved successfully"));
    }

    @PatchMapping("/years/{yearId}/activate")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> activateAcademicYear(@PathVariable UUID yearId, @RequestParam UUID schoolId) {
        return ResponseEntity.ok(ApiResponse.success(academicStructureService.activateAcademicYear(yearId, schoolId), "Academic year activated successfully"));
    }

    @PostMapping("/classes")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> createClass(@RequestBody AcademicClassRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(academicStructureService.createAcademicClass(request), "Class created successfully"));
    }

    @GetMapping("/classes")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getClasses(@RequestParam UUID schoolId,
                                                     @RequestParam(required = false) UUID academicYearId) {
        return ResponseEntity.ok(ApiResponse.success(academicStructureService.getAcademicClasses(schoolId, academicYearId), "Classes retrieved successfully"));
    }

    @PostMapping("/sections")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> createSection(@RequestBody AcademicSectionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(academicStructureService.createSection(request), "Section created successfully"));
    }

    @GetMapping("/sections")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getSections(@RequestParam UUID classId) {
        return ResponseEntity.ok(ApiResponse.success(academicStructureService.getSections(classId), "Sections retrieved successfully"));
    }

    @PostMapping("/streams")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> createStream(@RequestBody AcademicStreamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(academicStructureService.createStream(request), "Stream created successfully"));
    }

    @GetMapping("/streams")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getStreams(@RequestParam UUID classId) {
        return ResponseEntity.ok(ApiResponse.success(academicStructureService.getStreams(classId), "Streams retrieved successfully"));
    }

    @PostMapping("/subjects")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> createSubject(@RequestBody AcademicSubjectRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(academicStructureService.createSubject(request), "Subject created successfully"));
    }

    @GetMapping("/subjects")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getSubjects(@RequestParam UUID classId) {
        return ResponseEntity.ok(ApiResponse.success(academicStructureService.getSubjects(classId), "Subjects retrieved successfully"));
    }

    @PostMapping("/departments")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> createDepartment(@RequestBody AcademicDepartmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(academicStructureService.createDepartment(request), "Department created successfully"));
    }

    @GetMapping("/departments")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getDepartments(@RequestParam UUID schoolId) {
        return ResponseEntity.ok(ApiResponse.success(academicStructureService.getDepartments(schoolId), "Departments retrieved successfully"));
    }

    @PostMapping("/houses")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> createHouse(@RequestBody AcademicHouseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(academicStructureService.createHouse(request), "House created successfully"));
    }

    @GetMapping("/houses")
    @PreAuthorize("hasAuthority('ACADEMIC_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getHouses(@RequestParam UUID schoolId) {
        return ResponseEntity.ok(ApiResponse.success(academicStructureService.getHouses(schoolId), "Houses retrieved successfully"));
    }
}
