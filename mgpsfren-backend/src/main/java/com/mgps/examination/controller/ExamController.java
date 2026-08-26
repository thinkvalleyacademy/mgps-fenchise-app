package com.mgps.examination.controller;

import com.mgps.common.dto.ApiResponse;
import com.mgps.examination.dto.ExamDtos.*;
import com.mgps.examination.service.ExamScheduleService;
import com.mgps.examination.service.ExamService;
import com.mgps.examination.service.MarksService;
import com.mgps.user.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/exams")
@PreAuthorize("hasAuthority('EXAM_MANAGE')")
public class ExamController {

    private final ExamService examService;
    private final ExamScheduleService examScheduleService;
    private final MarksService marksService;

    public ExamController(ExamService examService, ExamScheduleService examScheduleService, MarksService marksService) {
        this.examService = examService;
        this.examScheduleService = examScheduleService;
        this.marksService = marksService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> createExam(@RequestBody ExamRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(examService.createExam(request), "Exam created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> listExams(@RequestParam UUID schoolId,
                                                     @RequestParam(required = false) UUID academicYearId) {
        return ResponseEntity.ok(ApiResponse.success(examService.getExams(schoolId, academicYearId), "Exams retrieved successfully"));
    }

    @GetMapping("/{examId}")
    public ResponseEntity<ApiResponse<?>> getExam(@PathVariable UUID examId) {
        return ResponseEntity.ok(ApiResponse.success(examService.getExam(examId), "Exam retrieved successfully"));
    }

    @PatchMapping("/{examId}/status")
    public ResponseEntity<ApiResponse<?>> updateStatus(@PathVariable UUID examId, @RequestBody ExamStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(examService.updateStatus(examId, request), "Exam status updated successfully"));
    }

    @PostMapping("/schedules")
    public ResponseEntity<ApiResponse<?>> addSchedule(@RequestBody ExamScheduleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(examScheduleService.addSchedule(request), "Exam schedule created successfully"));
    }

    @PostMapping("/schedules/check-conflict")
    public ResponseEntity<ApiResponse<?>> checkConflict(@RequestBody ExamScheduleRequest request) {
        return ResponseEntity.ok(ApiResponse.success(examScheduleService.checkConflict(request), "Conflict check completed"));
    }

    @GetMapping("/{examId}/schedules")
    public ResponseEntity<ApiResponse<?>> getSchedule(@PathVariable UUID examId) {
        return ResponseEntity.ok(ApiResponse.success(examScheduleService.getSchedule(examId), "Exam schedule retrieved successfully"));
    }

    @PostMapping("/marks")
    public ResponseEntity<ApiResponse<?>> enterMarks(@RequestBody MarkEntryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(marksService.enterMarks(request, currentUserId()), "Marks recorded successfully"));
    }

    @GetMapping("/schedules/{examScheduleId}/marks")
    public ResponseEntity<ApiResponse<?>> getMarksForSchedule(@PathVariable UUID examScheduleId) {
        return ResponseEntity.ok(ApiResponse.success(marksService.getMarksForSchedule(examScheduleId), "Marks retrieved successfully"));
    }

    @GetMapping("/{examId}/results/{studentId}")
    public ResponseEntity<ApiResponse<?>> getStudentResult(@PathVariable UUID examId, @PathVariable UUID studentId) {
        return ResponseEntity.ok(ApiResponse.success(marksService.getExamResult(examId, studentId), "Result retrieved successfully"));
    }

    @GetMapping("/{examId}/results")
    public ResponseEntity<ApiResponse<?>> getClassResults(@PathVariable UUID examId, @RequestParam UUID classId) {
        return ResponseEntity.ok(ApiResponse.success(marksService.getExamResults(examId, classId), "Results retrieved successfully"));
    }

    @GetMapping("/{examId}/subjects/{subjectId}/analysis")
    public ResponseEntity<ApiResponse<?>> getSubjectAnalysis(@PathVariable UUID examId, @PathVariable UUID subjectId) {
        return ResponseEntity.ok(ApiResponse.success(marksService.getSubjectAnalysis(examId, subjectId), "Subject analysis retrieved successfully"));
    }

    private UUID currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser principal) {
            return principal.getUserId();
        }
        return null;
    }
}
