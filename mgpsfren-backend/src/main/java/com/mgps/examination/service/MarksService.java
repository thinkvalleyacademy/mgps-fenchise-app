package com.mgps.examination.service;

import com.mgps.common.exception.BusinessLogicException;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.examination.dto.ExamDtos.*;
import com.mgps.examination.entity.Exam;
import com.mgps.examination.entity.ExamMark;
import com.mgps.examination.entity.ExamSchedule;
import com.mgps.examination.repository.ExamMarkRepository;
import com.mgps.examination.repository.ExamRepository;
import com.mgps.examination.repository.ExamScheduleRepository;
import com.mgps.student.entity.Student;
import com.mgps.student.repository.StudentRepository;
import com.mgps.tenant.TenantGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MarksService {

    private final ExamMarkRepository examMarkRepository;
    private final ExamScheduleRepository examScheduleRepository;
    private final ExamRepository examRepository;
    private final StudentRepository studentRepository;
    private final TenantGuard tenantGuard;

    public MarksService(ExamMarkRepository examMarkRepository, ExamScheduleRepository examScheduleRepository,
                        ExamRepository examRepository, StudentRepository studentRepository) {
        this(examMarkRepository, examScheduleRepository, examRepository, studentRepository, null);
    }

    @Autowired
    public MarksService(ExamMarkRepository examMarkRepository, ExamScheduleRepository examScheduleRepository,
                        ExamRepository examRepository, StudentRepository studentRepository,
                        TenantGuard tenantGuard) {
        this.examMarkRepository = examMarkRepository;
        this.examScheduleRepository = examScheduleRepository;
        this.examRepository = examRepository;
        this.studentRepository = studentRepository;
        this.tenantGuard = tenantGuard != null ? tenantGuard : new TenantGuard();
    }

    public List<ExamMarkResponse> enterMarks(MarkEntryRequest request, UUID enteredBy) {
        if (request.getExamScheduleId() == null || request.getEntries() == null || request.getEntries().isEmpty()) {
            throw new BusinessLogicException("examScheduleId and at least one entry are required");
        }

        ExamSchedule schedule = examScheduleRepository.findById(request.getExamScheduleId())
            .orElseThrow(() -> new ResourceNotFoundException("Exam schedule not found"));
        Exam exam = examRepository.findById(schedule.getExamId())
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        tenantGuard.assertSchoolAccessible(exam.getSchoolId());

        List<ExamMarkResponse> results = new ArrayList<>();
        for (MarkEntryItem item : request.getEntries()) {
            results.add(map(upsertMark(schedule, exam, item, enteredBy)));
        }
        return results;
    }

    private ExamMark upsertMark(ExamSchedule schedule, Exam exam, MarkEntryItem item, UUID enteredBy) {
        if (item.getStudentId() == null) {
            throw new BusinessLogicException("studentId is required for every mark entry");
        }
        if (!item.isAbsent()) {
            if (item.getMarksObtained() == null) {
                throw new BusinessLogicException("marksObtained is required unless the student is marked absent");
            }
            if (item.getMarksObtained().compareTo(BigDecimal.ZERO) < 0
                || item.getMarksObtained().compareTo(schedule.getMaxMarks()) > 0) {
                throw new BusinessLogicException("marksObtained must be between 0 and " + schedule.getMaxMarks());
            }
        }

        ExamMark mark = examMarkRepository.findByExamScheduleIdAndStudentId(schedule.getId(), item.getStudentId())
            .orElseGet(() -> new ExamMark(UUID.randomUUID(), schedule.getId(), exam.getId(), schedule.getSubjectId(),
                item.getStudentId(), null, false, null, enteredBy));

        mark.setAbsent(item.isAbsent());
        mark.setMarksObtained(item.isAbsent() ? null : item.getMarksObtained());
        mark.setRemarks(item.getRemarks());
        mark.setEnteredBy(enteredBy);
        return examMarkRepository.save(mark);
    }

    public List<ExamMarkResponse> getMarksForSchedule(UUID examScheduleId) {
        ExamSchedule schedule = examScheduleRepository.findById(examScheduleId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam schedule not found"));
        Exam exam = examRepository.findById(schedule.getExamId())
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        tenantGuard.assertSchoolAccessible(exam.getSchoolId());

        return examMarkRepository.findByExamScheduleId(examScheduleId).stream().map(this::map).collect(Collectors.toList());
    }

    public StudentResultResponse getExamResult(UUID examId, UUID studentId) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        tenantGuard.assertSchoolAccessible(exam.getSchoolId());
        return computeResult(exam, studentId);
    }

    public List<StudentResultResponse> getExamResults(UUID examId, UUID classId) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        tenantGuard.assertSchoolAccessible(exam.getSchoolId());

        List<StudentResultResponse> results = studentRepository.findByClassId(classId).stream()
            .map(student -> computeResult(exam, student.getId()))
            .sorted(Comparator.comparing(StudentResultResponse::getPercentage,
                Comparator.nullsLast(Comparator.reverseOrder())))
            .collect(Collectors.toList());

        for (int i = 0; i < results.size(); i++) {
            results.get(i).setRank(i + 1);
        }
        return results;
    }

    public SubjectAnalysisResponse getSubjectAnalysis(UUID examId, UUID subjectId) {
        Exam exam = examRepository.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        tenantGuard.assertSchoolAccessible(exam.getSchoolId());

        List<ExamSchedule> schedules = examScheduleRepository.findByExamIdAndSubjectId(examId, subjectId);
        BigDecimal passingMarks = schedules.stream()
            .map(ExamSchedule::getPassingMarks)
            .filter(java.util.Objects::nonNull)
            .findFirst()
            .orElse(null);

        List<ExamMark> marks = examMarkRepository.findByExamIdAndSubjectId(examId, subjectId).stream()
            .filter(m -> !m.isAbsent() && m.getMarksObtained() != null)
            .collect(Collectors.toList());

        SubjectAnalysisResponse response = new SubjectAnalysisResponse();
        response.setExamId(examId);
        response.setSubjectId(subjectId);
        response.setStudentsAppeared(marks.size());

        if (marks.isEmpty()) {
            response.setAverageMarks(BigDecimal.ZERO);
            response.setHighestMarks(BigDecimal.ZERO);
            response.setLowestMarks(BigDecimal.ZERO);
            response.setPassCount(0);
            response.setFailCount(0);
            return response;
        }

        BigDecimal sum = marks.stream().map(ExamMark::getMarksObtained).reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setAverageMarks(sum.divide(BigDecimal.valueOf(marks.size()), 2, RoundingMode.HALF_UP));
        response.setHighestMarks(marks.stream().map(ExamMark::getMarksObtained).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        response.setLowestMarks(marks.stream().map(ExamMark::getMarksObtained).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));

        if (passingMarks != null) {
            long passed = marks.stream().filter(m -> m.getMarksObtained().compareTo(passingMarks) >= 0).count();
            response.setPassCount((int) passed);
            response.setFailCount(marks.size() - (int) passed);
        }
        return response;
    }

    private StudentResultResponse computeResult(Exam exam, UUID studentId) {
        List<ExamMark> marks = examMarkRepository.findByExamIdAndStudentId(exam.getId(), studentId);

        BigDecimal totalObtained = BigDecimal.ZERO;
        BigDecimal totalMax = BigDecimal.ZERO;
        int appeared = 0;
        int absent = 0;

        Map<UUID, ExamSchedule> scheduleCache = new HashMap<>();
        for (ExamMark mark : marks) {
            ExamSchedule schedule = scheduleCache.computeIfAbsent(mark.getExamScheduleId(),
                id -> examScheduleRepository.findById(id).orElse(null));
            BigDecimal maxMarks = schedule != null ? schedule.getMaxMarks() : BigDecimal.ZERO;
            totalMax = totalMax.add(maxMarks);
            if (mark.isAbsent()) {
                absent++;
            } else {
                appeared++;
                totalObtained = totalObtained.add(mark.getMarksObtained() != null ? mark.getMarksObtained() : BigDecimal.ZERO);
            }
        }

        BigDecimal percentage = totalMax.compareTo(BigDecimal.ZERO) > 0
            ? totalObtained.multiply(BigDecimal.valueOf(100)).divide(totalMax, 2, RoundingMode.HALF_UP)
            : null;

        StudentResultResponse response = new StudentResultResponse();
        response.setStudentId(studentId);
        response.setExamId(exam.getId());
        response.setTotalMarksObtained(totalObtained);
        response.setTotalMaxMarks(totalMax);
        response.setPercentage(percentage);
        response.setGrade(computeGrade(percentage));
        response.setSubjectsAppeared(appeared);
        response.setSubjectsAbsent(absent);

        studentRepository.findById(studentId).ifPresent(student ->
            response.setStudentName(studentName(student)));

        return response;
    }

    private String studentName(Student student) {
        return student.getFirstName() + " " + student.getLastName();
    }

    /**
     * Fixed percentage bands, not a configurable grade-scale entity — a documented
     * simplification to keep this first pass bounded.
     */
    private String computeGrade(BigDecimal percentage) {
        if (percentage == null) {
            return null;
        }
        double p = percentage.doubleValue();
        if (p >= 90) return "A+";
        if (p >= 80) return "A";
        if (p >= 70) return "B";
        if (p >= 60) return "C";
        if (p >= 50) return "D";
        return "F";
    }

    private ExamMarkResponse map(ExamMark mark) {
        ExamMarkResponse response = new ExamMarkResponse();
        response.setMarkId(mark.getId());
        response.setExamScheduleId(mark.getExamScheduleId());
        response.setStudentId(mark.getStudentId());
        response.setSubjectId(mark.getSubjectId());
        response.setMarksObtained(mark.getMarksObtained());
        response.setAbsent(mark.isAbsent());
        response.setRemarks(mark.getRemarks());
        response.setEnteredBy(mark.getEnteredBy());
        response.setUpdatedAt(mark.getUpdatedAt());
        return response;
    }
}
