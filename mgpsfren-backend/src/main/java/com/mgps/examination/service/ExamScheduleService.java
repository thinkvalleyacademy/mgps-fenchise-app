package com.mgps.examination.service;

import com.mgps.common.exception.BusinessLogicException;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.examination.dto.ExamDtos.*;
import com.mgps.examination.entity.Exam;
import com.mgps.examination.entity.ExamSchedule;
import com.mgps.examination.repository.ExamRepository;
import com.mgps.examination.repository.ExamScheduleRepository;
import com.mgps.tenant.TenantGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamScheduleService {

    private final ExamScheduleRepository examScheduleRepository;
    private final ExamRepository examRepository;
    private final TenantGuard tenantGuard;

    public ExamScheduleService(ExamScheduleRepository examScheduleRepository, ExamRepository examRepository) {
        this(examScheduleRepository, examRepository, null);
    }

    @Autowired
    public ExamScheduleService(ExamScheduleRepository examScheduleRepository, ExamRepository examRepository,
                               TenantGuard tenantGuard) {
        this.examScheduleRepository = examScheduleRepository;
        this.examRepository = examRepository;
        this.tenantGuard = tenantGuard != null ? tenantGuard : new TenantGuard();
    }

    public ExamScheduleResponse addSchedule(ExamScheduleRequest request) {
        Exam exam = validateAndLoadExam(request);

        ExamConflictResponse conflict = checkConflictInternal(request, null);
        if (conflict.isConflict()) {
            throw new BusinessLogicException(conflict.getReason());
        }

        ExamSchedule schedule = new ExamSchedule(
            UUID.randomUUID(),
            exam.getId(),
            request.getSubjectId(),
            request.getExamDate(),
            request.getStartTime(),
            request.getEndTime(),
            request.getRoomNumber(),
            request.getMaxMarks(),
            request.getPassingMarks()
        );
        return map(examScheduleRepository.save(schedule));
    }

    public ExamConflictResponse checkConflict(ExamScheduleRequest request) {
        validateAndLoadExam(request);
        return checkConflictInternal(request, null);
    }

    public List<ExamScheduleResponse> getSchedule(UUID examId) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        tenantGuard.assertSchoolAccessible(exam.getSchoolId());
        return examScheduleRepository.findByExamId(examId).stream().map(this::map).collect(Collectors.toList());
    }

    ExamSchedule getScheduleEntity(UUID scheduleId) {
        return examScheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam schedule not found"));
    }

    private Exam validateAndLoadExam(ExamScheduleRequest request) {
        if (request.getExamId() == null || request.getSubjectId() == null || request.getExamDate() == null ||
            request.getStartTime() == null || request.getEndTime() == null || request.getMaxMarks() == null) {
            throw new BusinessLogicException("examId, subjectId, examDate, startTime, endTime and maxMarks are required");
        }
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BusinessLogicException("Start time must be before end time");
        }
        Exam exam = examRepository.findById(request.getExamId())
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        // The target school is derived from the exam itself, never trusted from the
        // request body, so a schedule can only ever be attached to an exam the
        // caller's own tenant already owns.
        tenantGuard.assertSchoolAccessible(exam.getSchoolId());
        return exam;
    }

    /**
     * Conflict detection scoped to schedules of the SAME exam: since an exam applies
     * to at most one class, any two of its papers that overlap in time mean a student
     * would need to sit two papers at once, which is always a conflict regardless of
     * room. A shared room on top of that gets a more specific reason. Cross-exam room
     * booking (two different exams sharing a room) is not checked — a documented
     * simplification, mirroring the same-day scoping TimetableService already uses.
     */
    private ExamConflictResponse checkConflictInternal(ExamScheduleRequest request, UUID excludeScheduleId) {
        List<ExamSchedule> existing = examScheduleRepository.findByExamId(request.getExamId());

        for (ExamSchedule schedule : existing) {
            if (excludeScheduleId != null && schedule.getId().equals(excludeScheduleId)) {
                continue;
            }
            if (!schedule.getExamDate().equals(request.getExamDate())) {
                continue;
            }
            if (!isOverlap(request.getStartTime(), request.getEndTime(), schedule.getStartTime(), schedule.getEndTime())) {
                continue;
            }
            if (schedule.getRoomNumber() != null && schedule.getRoomNumber().equalsIgnoreCase(request.getRoomNumber())) {
                return conflict("Room is already booked for this exam at this time");
            }
            return conflict("This exam already has a paper scheduled at an overlapping time");
        }

        return noConflict();
    }

    private boolean isOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private ExamConflictResponse conflict(String reason) {
        ExamConflictResponse response = new ExamConflictResponse();
        response.setConflict(true);
        response.setReason(reason);
        return response;
    }

    private ExamConflictResponse noConflict() {
        ExamConflictResponse response = new ExamConflictResponse();
        response.setConflict(false);
        response.setReason("No conflict detected");
        return response;
    }

    private ExamScheduleResponse map(ExamSchedule schedule) {
        ExamScheduleResponse response = new ExamScheduleResponse();
        response.setScheduleId(schedule.getId());
        response.setExamId(schedule.getExamId());
        response.setSubjectId(schedule.getSubjectId());
        response.setExamDate(schedule.getExamDate());
        response.setStartTime(schedule.getStartTime());
        response.setEndTime(schedule.getEndTime());
        response.setRoomNumber(schedule.getRoomNumber());
        response.setMaxMarks(schedule.getMaxMarks());
        response.setPassingMarks(schedule.getPassingMarks());
        response.setCreatedAt(schedule.getCreatedAt());
        return response;
    }
}
