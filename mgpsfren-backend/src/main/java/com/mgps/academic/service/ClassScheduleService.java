package com.mgps.academic.service;

import com.mgps.academic.dto.ClassScheduleDTO;
import com.mgps.academic.dto.DuplicateScheduleRequest;
import com.mgps.academic.entity.ClassSchedule;
import com.mgps.academic.repository.ClassScheduleRepository;
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

    public ClassScheduleService(ClassScheduleRepository repository) {
        this.repository = repository;
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
        existing.setStartTime(dto.getStartTime());
        existing.setEndTime(dto.getEndTime());
        existing.setScheduleType(dto.getScheduleType());
        existing.setSubject(dto.getSubject());
        existing.setContent(dto.getContent());
        existing.setLocation(dto.getLocation());
        existing.setTeacherName(dto.getTeacherName());
        
        return toDTO(repository.save(existing));
    }

    public void deleteSchedule(UUID id) {
        repository.deleteById(id);
    }

    public void duplicateSchedule(DuplicateScheduleRequest request) {
        int sourceWeekNumber = normalizeWeekNumber(request.getSourceWeekNumber());
        int targetWeekNumber = normalizeWeekNumber(request.getTargetWeekNumber());
        List<ClassSchedule> sourceSchedules = repository.findByClassNameAndAcademicSessionAndWeekNumber(
                request.getSourceClassName(), request.getSourceSession(), sourceWeekNumber);
        
        // Clear target schedules if they exist or just append? User said "duplicate it so it will be easy to create new"
        // Usually, duplication implies starting fresh or adding to. Let's append but could be changed to overwrite.
        
        List<ClassSchedule> duplicatedSchedules = sourceSchedules.stream().map(source -> {
            ClassSchedule target = new ClassSchedule();
            target.setClassName(request.getTargetClassName());
            target.setAcademicSession(request.getTargetSession());
            target.setWeekNumber(targetWeekNumber);
            target.setDayOfWeek(source.getDayOfWeek());
            target.setStartTime(source.getStartTime());
            target.setEndTime(source.getEndTime());
            target.setScheduleType(source.getScheduleType());
            target.setSubject(source.getSubject());
            target.setContent(source.getContent());
            target.setLocation(source.getLocation());
            target.setTeacherName(source.getTeacherName());
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
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setScheduleType(dto.getScheduleType());
        entity.setSubject(dto.getSubject());
        entity.setContent(dto.getContent());
        entity.setLocation(dto.getLocation());
        entity.setTeacherName(dto.getTeacherName());
        return entity;
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
