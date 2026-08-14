package com.mgps.examination.service;

import com.mgps.common.exception.BusinessLogicException;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.examination.dto.ExamDtos.*;
import com.mgps.examination.entity.Exam;
import com.mgps.examination.entity.ExamStatus;
import com.mgps.examination.repository.ExamRepository;
import com.mgps.tenant.TenantGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ExamService {

    private final ExamRepository examRepository;
    private final TenantGuard tenantGuard;

    public ExamService(ExamRepository examRepository) {
        this(examRepository, null);
    }

    @Autowired
    public ExamService(ExamRepository examRepository, TenantGuard tenantGuard) {
        this.examRepository = examRepository;
        this.tenantGuard = tenantGuard != null ? tenantGuard : new TenantGuard();
    }

    public ExamResponse createExam(ExamRequest request) {
        if (request.getSchoolId() == null || request.getAcademicYearId() == null || request.getName() == null) {
            throw new BusinessLogicException("schoolId, academicYearId and name are required");
        }
        tenantGuard.assertSchoolAccessible(request.getSchoolId());

        Exam exam = new Exam(
            UUID.randomUUID(),
            request.getSchoolId(),
            request.getAcademicYearId(),
            request.getClassId(),
            request.getName(),
            request.getExamType(),
            request.getStartDate(),
            request.getEndDate(),
            ExamStatus.SCHEDULED
        );
        return map(examRepository.save(exam));
    }

    public List<ExamResponse> getExams(UUID schoolId, UUID academicYearId) {
        tenantGuard.assertSchoolAccessible(schoolId);
        List<Exam> exams = academicYearId != null
            ? examRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId)
            : examRepository.findBySchoolId(schoolId);
        return exams.stream().map(this::map).collect(Collectors.toList());
    }

    public ExamResponse getExam(UUID examId) {
        return map(getExamEntity(examId));
    }

    public ExamResponse updateStatus(UUID examId, ExamStatusUpdateRequest request) {
        if (request.getStatus() == null) {
            throw new BusinessLogicException("status is required");
        }
        Exam exam = getExamEntity(examId);
        exam.setStatus(request.getStatus());
        return map(examRepository.save(exam));
    }

    Exam getExamEntity(UUID examId) {
        return examRepository.findById(examId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
    }

    private ExamResponse map(Exam exam) {
        ExamResponse response = new ExamResponse();
        response.setExamId(exam.getId());
        response.setSchoolId(exam.getSchoolId());
        response.setAcademicYearId(exam.getAcademicYearId());
        response.setClassId(exam.getClassId());
        response.setName(exam.getName());
        response.setExamType(exam.getExamType());
        response.setStartDate(exam.getStartDate());
        response.setEndDate(exam.getEndDate());
        response.setStatus(exam.getStatus());
        response.setCreatedAt(exam.getCreatedAt());
        response.setUpdatedAt(exam.getUpdatedAt());
        return response;
    }
}
