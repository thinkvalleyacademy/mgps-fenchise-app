package com.mgps.academic.service;

import com.mgps.academic.dto.AcademicDtos.*;
import com.mgps.academic.entity.*;
import com.mgps.academic.repository.*;
import com.mgps.common.exception.BusinessLogicException;
import com.mgps.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AcademicStructureService {

    private final AcademicYearRepository academicYearRepository;
    private final AcademicClassRepository academicClassRepository;
    private final AcademicSectionRepository academicSectionRepository;
    private final AcademicStreamRepository academicStreamRepository;
    private final AcademicSubjectRepository academicSubjectRepository;
    private final AcademicDepartmentRepository academicDepartmentRepository;
    private final AcademicHouseRepository academicHouseRepository;

    public AcademicStructureService(AcademicYearRepository academicYearRepository,
                                    AcademicClassRepository academicClassRepository,
                                    AcademicSectionRepository academicSectionRepository,
                                    AcademicStreamRepository academicStreamRepository,
                                    AcademicSubjectRepository academicSubjectRepository,
                                    AcademicDepartmentRepository academicDepartmentRepository,
                                    AcademicHouseRepository academicHouseRepository) {
        this.academicYearRepository = academicYearRepository;
        this.academicClassRepository = academicClassRepository;
        this.academicSectionRepository = academicSectionRepository;
        this.academicStreamRepository = academicStreamRepository;
        this.academicSubjectRepository = academicSubjectRepository;
        this.academicDepartmentRepository = academicDepartmentRepository;
        this.academicHouseRepository = academicHouseRepository;
    }

    public AcademicYearResponse createAcademicYear(AcademicYearRequest request) {
        if (request.getSchoolId() == null) {
            throw new BusinessLogicException("School ID is required");
        }
        AcademicYear year = new AcademicYear(
            UUID.randomUUID(),
            request.getSchoolId(),
            request.getName(),
            request.getStartDate(),
            request.getEndDate(),
            false,
            null,
            null
        );
        AcademicYear saved = academicYearRepository.save(year);
        return mapYear(saved);
    }

    public List<AcademicYearResponse> getAcademicYears(UUID schoolId) {
        return academicYearRepository.findBySchoolId(schoolId).stream().map(this::mapYear).collect(Collectors.toList());
    }

    public AcademicYearResponse activateAcademicYear(UUID yearId, UUID schoolId) {
        academicYearRepository.findBySchoolId(schoolId).forEach(year -> {
            if (Boolean.TRUE.equals(year.getIsActive())) {
                year.setIsActive(false);
                academicYearRepository.save(year);
            }
        });

        AcademicYear year = academicYearRepository.findById(yearId)
            .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));
        year.setIsActive(true);
        return mapYear(academicYearRepository.save(year));
    }

    public AcademicClassResponse createAcademicClass(AcademicClassRequest request) {
        if (request.getSchoolId() == null) {
            throw new BusinessLogicException("School ID is required");
        }
        UUID yearId = request.getAcademicYearId();
        if (yearId == null) {
            yearId = academicYearRepository.findBySchoolIdAndIsActiveTrue(request.getSchoolId())
                .map(AcademicYear::getId)
                .orElseThrow(() -> new ResourceNotFoundException("No active academic year found. Please activate one first."));
        }

        AcademicClass academicClass = new AcademicClass(
            UUID.randomUUID(),
            request.getSchoolId(),
            yearId,
            request.getName(),
            request.getGradeLevel(),
            request.getCode(),
            request.getDescription(),
            true,
            null,
            null
        );
        return mapClass(academicClassRepository.save(academicClass));
    }

    public List<AcademicClassResponse> getAcademicClasses(UUID schoolId, UUID academicYearId) {
        List<AcademicClass> classes = academicYearId != null
            ? academicClassRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId)
            : academicClassRepository.findBySchoolId(schoolId);
        return classes.stream().map(this::mapClass).collect(Collectors.toList());
    }

    public AcademicSimpleResponse createSection(AcademicSectionRequest request) {
        if (request.getSchoolId() == null) {
            throw new BusinessLogicException("School ID is required");
        }
        AcademicSection section = new AcademicSection(
            UUID.randomUUID(),
            request.getSchoolId(),
            request.getClassId(),
            request.getName(),
            request.getCapacity(),
            true,
            null,
            null
        );
        AcademicSection saved = academicSectionRepository.save(section);
        return mapSection(saved);
    }

    public List<AcademicSimpleResponse> getSections(UUID classId) {
        return academicSectionRepository.findByClassId(classId).stream()
            .map(this::mapSection)
            .collect(Collectors.toList());
    }

    public AcademicSimpleResponse createStream(AcademicStreamRequest request) {
        if (request.getSchoolId() == null) {
            throw new BusinessLogicException("School ID is required");
        }
        AcademicStream stream = new AcademicStream(
            UUID.randomUUID(),
            request.getSchoolId(),
            request.getClassId(),
            request.getName(),
            request.getDescription(),
            true,
            null,
            null
        );
        AcademicStream saved = academicStreamRepository.save(stream);
        return mapSimple(saved.getId(), saved.getSchoolId(), saved.getName(), saved.getIsActive(), saved.getCreatedAt());
    }

    public List<AcademicSimpleResponse> getStreams(UUID classId) {
        return academicStreamRepository.findByClassId(classId).stream()
            .map(stream -> mapSimple(stream.getId(), stream.getSchoolId(), stream.getName(), stream.getIsActive(), stream.getCreatedAt()))
            .collect(Collectors.toList());
    }

    public AcademicSimpleResponse createSubject(AcademicSubjectRequest request) {
        if (request.getSchoolId() == null) {
            throw new BusinessLogicException("School ID is required");
        }
        AcademicSubject subject = new AcademicSubject(
            UUID.randomUUID(),
            request.getSchoolId(),
            request.getClassId(),
            request.getName(),
            request.getCode(),
            request.getSubjectType(),
            request.getDescription(),
            true,
            null,
            null
        );
        AcademicSubject saved = academicSubjectRepository.save(subject);
        return mapSimple(saved.getId(), saved.getSchoolId(), saved.getName(), saved.getCode(), saved.getSubjectType(), saved.getIsActive(), saved.getCreatedAt());
    }

    public List<AcademicSimpleResponse> getSubjects(UUID classId) {
        return academicSubjectRepository.findByClassId(classId).stream()
            .map(subject -> mapSimple(subject.getId(), subject.getSchoolId(), subject.getName(), subject.getCode(), subject.getSubjectType(), subject.getIsActive(), subject.getCreatedAt()))
            .collect(Collectors.toList());
    }

    public AcademicSimpleResponse createDepartment(AcademicDepartmentRequest request) {
        if (request.getSchoolId() == null) {
            throw new BusinessLogicException("School ID is required");
        }
        AcademicDepartment department = new AcademicDepartment(
            UUID.randomUUID(),
            request.getSchoolId(),
            request.getName(),
            request.getCode(),
            request.getHeadName(),
            true,
            null,
            null
        );
        AcademicDepartment saved = academicDepartmentRepository.save(department);
        return mapSimple(saved.getId(), saved.getSchoolId(), saved.getName(), saved.getIsActive(), saved.getCreatedAt());
    }

    public List<AcademicSimpleResponse> getDepartments(UUID schoolId) {
        return academicDepartmentRepository.findBySchoolId(schoolId).stream()
            .map(department -> mapSimple(department.getId(), department.getSchoolId(), department.getName(), department.getIsActive(), department.getCreatedAt()))
            .collect(Collectors.toList());
    }

    public AcademicSimpleResponse createHouse(AcademicHouseRequest request) {
        if (request.getSchoolId() == null) {
            throw new BusinessLogicException("School ID is required");
        }
        AcademicHouse house = new AcademicHouse(
            UUID.randomUUID(),
            request.getSchoolId(),
            request.getName(),
            request.getColor(),
            request.getMotto(),
            true,
            null,
            null
        );
        AcademicHouse saved = academicHouseRepository.save(house);
        return mapSimple(saved.getId(), saved.getSchoolId(), saved.getName(), saved.getIsActive(), saved.getCreatedAt());
    }

    public List<AcademicSimpleResponse> getHouses(UUID schoolId) {
        return academicHouseRepository.findBySchoolId(schoolId).stream()
            .map(house -> mapSimple(house.getId(), house.getSchoolId(), house.getName(), house.getIsActive(), house.getCreatedAt()))
            .collect(Collectors.toList());
    }

    private AcademicYearResponse mapYear(AcademicYear year) {
        AcademicYearResponse response = new AcademicYearResponse();
        response.setYearId(year.getId());
        response.setSchoolId(year.getSchoolId());
        response.setName(year.getName());
        response.setStartDate(year.getStartDate());
        response.setEndDate(year.getEndDate());
        response.setIsActive(year.getIsActive());
        response.setCreatedAt(year.getCreatedAt());
        response.setUpdatedAt(year.getUpdatedAt());
        return response;
    }

    private AcademicClassResponse mapClass(AcademicClass academicClass) {
        AcademicClassResponse response = new AcademicClassResponse();
        response.setClassId(academicClass.getId());
        response.setSchoolId(academicClass.getSchoolId());
        response.setAcademicYearId(academicClass.getAcademicYearId());
        response.setName(academicClass.getName());
        response.setCode(academicClass.getCode());
        response.setGradeLevel(academicClass.getGradeLevel());
        response.setDescription(academicClass.getDescription());
        response.setSectionCount(academicSectionRepository.findByClassId(academicClass.getId()).size());
        response.setIsActive(academicClass.getIsActive());
        response.setCreatedAt(academicClass.getCreatedAt());
        response.setUpdatedAt(academicClass.getUpdatedAt());
        return response;
    }

    private AcademicSimpleResponse mapSimple(UUID id, UUID schoolId, String name, Boolean isActive, java.time.LocalDateTime createdAt) {
        AcademicSimpleResponse response = new AcademicSimpleResponse();
        response.setId(id);
        response.setSchoolId(schoolId);
        response.setName(name);
        response.setIsActive(isActive);
        response.setCreatedAt(createdAt);
        return response;
    }

    private AcademicSimpleResponse mapSection(AcademicSection section) {
        AcademicSimpleResponse response = mapSimple(section.getId(), section.getSchoolId(), section.getName(), section.getIsActive(), section.getCreatedAt());
        response.setCapacity(section.getCapacity());
        return response;
    }

    private AcademicSimpleResponse mapSimple(UUID id, UUID schoolId, String name, String code, String subjectType, Boolean isActive, java.time.LocalDateTime createdAt) {
        AcademicSimpleResponse response = new AcademicSimpleResponse();
        response.setId(id);
        response.setSchoolId(schoolId);
        response.setName(name);
        response.setCode(code);
        response.setSubjectType(subjectType);
        response.setIsActive(isActive);
        response.setCreatedAt(createdAt);
        return response;
    }
}
