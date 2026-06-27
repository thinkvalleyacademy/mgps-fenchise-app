package com.mgps.academic.service;

import com.mgps.academic.dto.ClassScheduleDTO;
import com.mgps.academic.dto.ClassSchedulePeriodDTO;
import com.mgps.academic.dto.DuplicateScheduleRequest;
import com.mgps.academic.entity.ClassSchedule;
import com.mgps.academic.entity.ClassSchedulePeriod;
import com.mgps.academic.repository.ClassSchedulePeriodRepository;
import com.mgps.academic.repository.ClassScheduleRepository;
import com.mgps.common.exception.BusinessLogicException;
import com.mgps.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClassScheduleService {

    private final ClassScheduleRepository repository;
    private final ClassSchedulePeriodRepository periodRepository;

    public ClassScheduleService(ClassScheduleRepository repository, ClassSchedulePeriodRepository periodRepository) {
        this.repository = repository;
        this.periodRepository = periodRepository;
    }

    public List<ClassScheduleDTO> getSchedules(String className, String session, Integer weekNumber) {
        int normalizedWeek = normalizeWeekNumber(weekNumber);
        return repository.findByClassNameAndAcademicSessionAndWeekNumber(className, session, normalizedWeek)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ClassScheduleDTO createSchedule(ClassScheduleDTO dto) {
        ClassSchedule entity = toEntity(dto);
        return toDTO(repository.save(entity));
    }

    public ClassScheduleDTO updateSchedule(UUID id, ClassScheduleDTO dto) {
        ClassSchedule existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found"));
        
        existing.setDayOfWeek(dto.getDayOfWeek());
        existing.setWeekNumber(normalizeWeekNumber(dto.getWeekNumber()));
        ClassSchedulePeriod period = resolvePeriod(existing.getClassName(), existing.getAcademicSession(), dto.getPeriodName());
        existing.setPeriodName(period.getPeriodName());
        existing.setStartTime(period.getStartTime());
        existing.setEndTime(period.getEndTime());
        existing.setScheduleType(dto.getScheduleType());
        existing.setSubject(dto.getSubject());
        existing.setContent(dto.getContent());
        existing.setLocation(null);
        existing.setTeacherName(null);
        
        return toDTO(repository.save(existing));
    }

    public void deleteSchedule(UUID id) {
        repository.deleteById(id);
    }

    public void duplicateSchedule(DuplicateScheduleRequest request) {
        int sourceWeekNumber = normalizeWeekNumber(request.getSourceWeekNumber());
        int targetWeekNumber = normalizeWeekNumber(request.getTargetWeekNumber());

        if (isBlank(request.getSourceClassName()) || isBlank(request.getSourceSession()) ||
                isBlank(request.getTargetClassName()) || isBlank(request.getTargetSession())) {
            throw new BusinessLogicException("All source and target fields are required for duplication");
        }

        List<ClassSchedule> sourceSchedules = repository.findByClassNameAndAcademicSessionAndWeekNumber(
                request.getSourceClassName(), request.getSourceSession(), sourceWeekNumber);

        if (sourceSchedules.isEmpty()) {
            throw new ResourceNotFoundException("No schedules found to duplicate for source week");
        }

        if (request.getSourceClassName().equals(request.getTargetClassName())
                && request.getSourceSession().equals(request.getTargetSession())
                && sourceWeekNumber == targetWeekNumber) {
            throw new BusinessLogicException("Target week must be different from source week when duplicating within the same class and session");
        }

        List<ClassSchedule> existingTarget = repository.findByClassNameAndAcademicSessionAndWeekNumber(
                request.getTargetClassName(), request.getTargetSession(), targetWeekNumber);
        if (!existingTarget.isEmpty()) {
            repository.deleteAll(existingTarget);
        }

        List<ClassSchedule> duplicatedSchedules = sourceSchedules.stream().map(source -> {
            ClassSchedule target = new ClassSchedule();
            target.setClassName(request.getTargetClassName());
            target.setAcademicSession(request.getTargetSession());
            target.setWeekNumber(targetWeekNumber);
            target.setDayOfWeek(source.getDayOfWeek());
            ClassSchedulePeriod period = resolvePeriod(request.getTargetClassName(), request.getTargetSession(), source.getPeriodName());
            target.setPeriodName(period.getPeriodName());
            target.setStartTime(period.getStartTime());
            target.setEndTime(period.getEndTime());
            target.setScheduleType(source.getScheduleType());
            target.setSubject(source.getSubject());
            target.setContent(source.getContent());
            target.setLocation(null);
            target.setTeacherName(null);
            return target;
        }).collect(Collectors.toList());

        repository.saveAll(duplicatedSchedules);
    }

    private ClassScheduleDTO toDTO(ClassSchedule entity) {
        ClassScheduleDTO dto = new ClassScheduleDTO();
        dto.setId(entity.getId());
        dto.setClassName(entity.getClassName());
        dto.setAcademicSession(entity.getAcademicSession());
        dto.setWeekNumber(entity.getWeekNumber());
        dto.setPeriodName(entity.getPeriodName());
        dto.setDayOfWeek(entity.getDayOfWeek());
        dto.setStartTime(entity.getStartTime());
        dto.setEndTime(entity.getEndTime());
        dto.setScheduleType(entity.getScheduleType());
        dto.setSubject(entity.getSubject());
        dto.setContent(entity.getContent());
        dto.setLocation(entity.getLocation());
        dto.setTeacherName(entity.getTeacherName());
        return dto;
    }

    private ClassSchedule toEntity(ClassScheduleDTO dto) {
        ClassSchedule entity = new ClassSchedule();
        entity.setClassName(dto.getClassName());
        entity.setAcademicSession(dto.getAcademicSession());
        entity.setWeekNumber(normalizeWeekNumber(dto.getWeekNumber()));
        entity.setDayOfWeek(dto.getDayOfWeek());
        ClassSchedulePeriod period = resolvePeriod(dto.getClassName(), dto.getAcademicSession(), dto.getPeriodName());
        entity.setPeriodName(period.getPeriodName());
        entity.setStartTime(period.getStartTime());
        entity.setEndTime(period.getEndTime());
        entity.setScheduleType(dto.getScheduleType());
        entity.setSubject(dto.getSubject());
        entity.setContent(dto.getContent());
        entity.setLocation(null);
        entity.setTeacherName(null);
        return entity;
    }

    public List<ClassSchedulePeriodDTO> getPeriods(String className, String session) {
        return periodRepository.findByClassNameAndAcademicSessionOrderByDisplayOrderAsc(className, session)
                .stream()
                .map(this::toPeriodDTO)
                .collect(Collectors.toList());
    }

    public ClassSchedulePeriodDTO createPeriod(ClassSchedulePeriodDTO dto) {
        if (isBlank(dto.getClassName()) || isBlank(dto.getAcademicSession()) || isBlank(dto.getPeriodName())) {
            throw new BusinessLogicException("Class, session and period name are required");
        }
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            throw new BusinessLogicException("Period start and end time are required");
        }
        if (!dto.getStartTime().isBefore(dto.getEndTime())) {
            throw new BusinessLogicException("Period start time must be before end time");
        }

        ClassSchedulePeriod period = periodRepository
                .findByClassNameAndAcademicSessionAndPeriodName(dto.getClassName(), dto.getAcademicSession(), normalizePeriodName(dto.getPeriodName()))
                .orElseGet(ClassSchedulePeriod::new);

        period.setClassName(dto.getClassName());
        period.setAcademicSession(dto.getAcademicSession());
        period.setPeriodName(normalizePeriodName(dto.getPeriodName()));
        period.setDisplayOrder(dto.getDisplayOrder() != null ? dto.getDisplayOrder() : nextPeriodOrder(dto.getClassName(), dto.getAcademicSession()));
        period.setStartTime(dto.getStartTime());
        period.setEndTime(dto.getEndTime());

        return toPeriodDTO(periodRepository.save(period));
    }

    public void deletePeriod(UUID id) {
        periodRepository.deleteById(id);
    }

    private ClassSchedulePeriod resolvePeriod(String className, String academicSession, String periodName) {
        if (isBlank(periodName)) {
            throw new BusinessLogicException("Period is required");
        }
        return periodRepository.findByClassNameAndAcademicSessionAndPeriodName(className, academicSession, normalizePeriodName(periodName))
                .orElseThrow(() -> new BusinessLogicException("Selected period is not configured for this class"));
    }

    private int nextPeriodOrder(String className, String session) {
        return periodRepository.findByClassNameAndAcademicSessionOrderByDisplayOrderAsc(className, session)
                .stream()
                .map(ClassSchedulePeriod::getDisplayOrder)
                .filter(order -> order != null)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    private ClassSchedulePeriodDTO toPeriodDTO(ClassSchedulePeriod period) {
        ClassSchedulePeriodDTO dto = new ClassSchedulePeriodDTO();
        dto.setId(period.getId());
        dto.setClassName(period.getClassName());
        dto.setAcademicSession(period.getAcademicSession());
        dto.setPeriodName(period.getPeriodName());
        dto.setDisplayOrder(period.getDisplayOrder());
        dto.setStartTime(period.getStartTime());
        dto.setEndTime(period.getEndTime());
        return dto;
    }

    private String normalizePeriodName(String periodName) {
        return periodName == null ? null : periodName.trim().toUpperCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private int normalizeWeekNumber(Integer weekNumber) {
        if (weekNumber == null) {
            return 1;
        }
        if (weekNumber < 1) {
            return 1;
        }
        if (weekNumber > 52) {
            return 52;
        }
        return weekNumber;
    }
}
