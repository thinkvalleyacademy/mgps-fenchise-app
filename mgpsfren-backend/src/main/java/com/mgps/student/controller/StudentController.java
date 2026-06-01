package com.mgps.student.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.student.dto.StudentDtos.*;
import com.mgps.student.service.StudentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping("/admission")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> admit(@RequestBody StudentAdmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(studentService.admitStudent(request), "Student admitted successfully"));
    }

    @GetMapping("/{studentId}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> getStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudent(studentId), "Student retrieved successfully"));
    }

    @PutMapping("/{studentId}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> updateStudent(@PathVariable UUID studentId,
                                                        @RequestBody StudentUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(studentService.updateStudent(studentId, request), "Student updated successfully"));
    }

    @DeleteMapping("/{studentId}")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> deleteStudent(@PathVariable UUID studentId) {
        studentService.deleteStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(null, "Student deleted successfully"));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> listStudents(@RequestParam UUID schoolId,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<StudentResponse> students = studentService.getStudentsBySchool(schoolId, pageable);
        return ResponseEntity.ok(ApiResponse.success(students, "Students retrieved successfully"));
    }

    @GetMapping("/class")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> listByClass(@RequestParam UUID schoolId,
                                                      @RequestParam UUID classId) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getStudentsByClass(schoolId, classId), "Class students retrieved successfully"));
    }

    @PatchMapping("/{studentId}/assign-class")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> assignClass(@PathVariable UUID studentId,
                                                      @RequestBody StudentAssignmentRequest request) {
        return ResponseEntity.ok(ApiResponse.success(studentService.assignClass(studentId, request), "Student assigned successfully"));
    }

    @PatchMapping("/{studentId}/promote")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> promote(@PathVariable UUID studentId,
                                                  @RequestBody StudentPromotionRequest request) {
        return ResponseEntity.ok(ApiResponse.success(studentService.promoteStudent(studentId, request), "Student promoted successfully"));
    }

    @PatchMapping("/{studentId}/transfer")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> transfer(@PathVariable UUID studentId,
                                                   @RequestBody StudentTransferRequest request) {
        return ResponseEntity.ok(ApiResponse.success(studentService.transferStudent(studentId, request), "Student transfer updated successfully"));
    }

    @PostMapping("/{studentId}/attendance")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> markAttendance(@PathVariable UUID studentId,
                                                         @RequestBody StudentAttendanceRequest request) {
        return ResponseEntity.ok(ApiResponse.success(studentService.markAttendance(studentId, request), "Attendance marked successfully"));
    }

    @GetMapping("/{studentId}/attendance/summary")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> attendanceSummary(@PathVariable UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getAttendanceSummary(studentId), "Attendance summary retrieved successfully"));
    }

    @PostMapping("/{studentId}/documents")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> uploadDocument(@PathVariable UUID studentId,
                                                         @RequestBody StudentDocumentUploadRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(studentService.uploadDocument(studentId, request), "Student document uploaded successfully"));
    }

    @GetMapping("/{studentId}/documents")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> listDocuments(@PathVariable UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success(studentService.getDocuments(studentId), "Student documents retrieved successfully"));
    }

    @GetMapping("/{studentId}/transfer-certificate")
    @PreAuthorize("hasAuthority('STUDENT_MANAGE')")
    public ResponseEntity<ApiResponse<?>> transferCertificate(@PathVariable UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success(studentService.generateTransferCertificate(studentId), "Transfer certificate generated successfully"));
    }
}
