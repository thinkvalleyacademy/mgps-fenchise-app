package com.mgps.academic.service;

import com.mgps.academic.dto.TimetableDtos.TimetableConflictResponse;
import com.mgps.academic.dto.TimetableDtos.TimetableRequest;
import com.mgps.academic.dto.TimetableDtos.TimetableResponse;
import com.mgps.academic.entity.TimetableEntry;
import com.mgps.academic.repository.TimetableEntryRepository;
import com.mgps.common.exception.BusinessLogicException;
import com.mgps.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class TimetableService {

    private final TimetableEntryRepository timetableEntryRepository;

    public TimetableService(TimetableEntryRepository timetableEntryRepository) {
        this.timetableEntryRepository = timetableEntryRepository;
    }

    public TimetableResponse createTimetableEntry(TimetableRequest request) {
        validateRequest(request);
        TimetableConflictResponse conflict = checkConflict(request);
        if (conflict.isConflict()) {
            throw new BusinessLogicException(conflict.getReason());
        }

        TimetableEntry entry = new TimetableEntry(
            UUID.randomUUID(),
            request.getSchoolId(),
            request.getAcademicYearId(),
            request.getClassId(),
            request.getSectionId(),
            request.getSubjectId(),
            request.getTeacherId(),
            request.getRoomName(),
            normalizeDay(request.getDayOfWeek()),
            request.getStartTime(),
            request.getEndTime(),
            request.getNotes(),
            true,
            null,
            null
        );
        return map(timetableEntryRepository.save(entry));
    }

    public TimetableConflictResponse checkConflict(TimetableRequest request) {
        validateRequest(request);
        String day = normalizeDay(request.getDayOfWeek());
        List<TimetableEntry> sameDayEntries = timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndDayOfWeek(
            request.getSchoolId(), request.getAcademicYearId(), day);

        for (TimetableEntry entry : sameDayEntries) {
            if (!isTimeOverlap(request.getStartTime(), request.getEndTime(), entry.getStartTime(), entry.getEndTime())) {
                continue;
            }
            if (entry.getTeacherId().equals(request.getTeacherId())) {
                return conflict("Teacher is already scheduled at this time");
            }
            if (entry.getRoomName().equalsIgnoreCase(request.getRoomName())) {
                return conflict("Room is already booked at this time");
            }
            if (entry.getClassId().equals(request.getClassId()) &&
                (request.getSectionId() == null || request.getSectionId().equals(entry.getSectionId()))) {
                return conflict("Class/section already has a timetable entry at this time");
            }
        }

        return noConflict();
    }

    public List<TimetableResponse> getTimetableForClass(UUID schoolId, UUID academicYearId, UUID classId) {
        return timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndClassId(schoolId, academicYearId, classId)
            .stream().map(this::map).collect(Collectors.toList());
    }

    public List<TimetableResponse> getTimetableForDay(UUID schoolId, UUID academicYearId, String dayOfWeek) {
        return timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndDayOfWeek(schoolId, academicYearId, normalizeDay(dayOfWeek))
            .stream().map(this::map).collect(Collectors.toList());
    }

    public List<TimetableResponse> getWeeklySchedule(UUID schoolId, UUID academicYearId) {
        return timetableEntryRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId)
            .stream().map(this::map).collect(Collectors.toList());
    }

    private void validateRequest(TimetableRequest request) {
        if (request == null) {
            throw new ResourceNotFoundException("Timetable request is required");
        }
        if (request.getSchoolId() == null || request.getAcademicYearId() == null || request.getClassId() == null ||
            request.getSubjectId() == null || request.getTeacherId() == null || request.getRoomName() == null ||
            request.getDayOfWeek() == null || request.getStartTime() == null || request.getEndTime() == null) {
            throw new ResourceNotFoundException("Missing timetable fields");
        }
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new BusinessLogicException("Start time must be before end time");
        }
    }

    private String normalizeDay(String dayOfWeek) {
        return DayOfWeek.valueOf(dayOfWeek.trim().toUpperCase()).name();
    }

    private boolean isTimeOverlap(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    private TimetableConflictResponse conflict(String reason) {
        TimetableConflictResponse response = new TimetableConflictResponse();
        response.setConflict(true);
        response.setReason(reason);
        return response;
    }

    private TimetableConflictResponse noConflict() {
        TimetableConflictResponse response = new TimetableConflictResponse();
        response.setConflict(false);
        response.setReason("No conflict detected");
        return response;
    }

    private TimetableResponse map(TimetableEntry entry) {
        TimetableResponse response = new TimetableResponse();
        response.setTimetableId(entry.getId());
        response.setSchoolId(entry.getSchoolId());
        response.setAcademicYearId(entry.getAcademicYearId());
        response.setClassId(entry.getClassId());
        response.setSectionId(entry.getSectionId());
        response.setSubjectId(entry.getSubjectId());
        response.setTeacherId(entry.getTeacherId());
        response.setRoomName(entry.getRoomName());
        response.setDayOfWeek(entry.getDayOfWeek());
        response.setStartTime(entry.getStartTime());
        response.setEndTime(entry.getEndTime());
        response.setNotes(entry.getNotes());
        response.setIsActive(entry.getIsActive());
        response.setCreatedAt(entry.getCreatedAt());
        response.setUpdatedAt(entry.getUpdatedAt());
        return response;
    }
}
