package com.mgps.academic.service;

import com.mgps.academic.dto.TimetableDtos.TimetableRequest;
import com.mgps.academic.entity.TimetableEntry;
import com.mgps.academic.repository.TimetableEntryRepository;
import com.mgps.common.exception.BusinessLogicException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TimetableServiceTest {

    @Mock
    private TimetableEntryRepository timetableEntryRepository;

    @InjectMocks
    private TimetableService service;

    private UUID schoolId;
    private UUID academicYearId;
    private UUID classId;
    private UUID subjectId;
    private UUID teacherId;

    @BeforeEach
    void setUp() {
        schoolId = UUID.randomUUID();
        academicYearId = UUID.randomUUID();
        classId = UUID.randomUUID();
        subjectId = UUID.randomUUID();
        teacherId = UUID.randomUUID();
    }

    @Test
    void shouldCreateTimetableEntry() {
        TimetableRequest request = baseRequest();
        TimetableEntry saved = new TimetableEntry(
            UUID.randomUUID(), schoolId, academicYearId, classId, null, subjectId, teacherId,
            "Room 1", "MONDAY", LocalTime.of(9, 0), LocalTime.of(10, 0), null, true, null, null
        );

        when(timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndDayOfWeek(schoolId, academicYearId, "MONDAY"))
            .thenReturn(List.of());
        when(timetableEntryRepository.save(any(TimetableEntry.class))).thenReturn(saved);

        var response = service.createTimetableEntry(request);

        assertThat(response.getSchoolId()).isEqualTo(schoolId);
        assertThat(response.getDayOfWeek()).isEqualTo("MONDAY");
        assertThat(response.getRoomName()).isEqualTo("Room 1");
    }

    @Test
    void shouldDetectTeacherConflict() {
        TimetableRequest request = baseRequest();
        TimetableEntry existing = new TimetableEntry(
            UUID.randomUUID(), schoolId, academicYearId, classId, null, UUID.randomUUID(), teacherId,
            "Room 2", "MONDAY", LocalTime.of(9, 30), LocalTime.of(10, 30), null, true, null, null
        );

        when(timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndDayOfWeek(schoolId, academicYearId, "MONDAY"))
            .thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.createTimetableEntry(request))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessageContaining("Teacher is already scheduled");
    }

    @Test
    void shouldDetectRoomConflict() {
        TimetableRequest request = baseRequest();
        request.setTeacherId(UUID.randomUUID());
        TimetableEntry existing = new TimetableEntry(
            UUID.randomUUID(), schoolId, academicYearId, classId, null, UUID.randomUUID(), teacherId,
            "Room 1", "MONDAY", LocalTime.of(9, 30), LocalTime.of(10, 30), null, true, null, null
        );

        when(timetableEntryRepository.findBySchoolIdAndAcademicYearIdAndDayOfWeek(schoolId, academicYearId, "MONDAY"))
            .thenReturn(List.of(existing));

        assertThatThrownBy(() -> service.createTimetableEntry(request))
            .isInstanceOf(BusinessLogicException.class)
            .hasMessageContaining("Room is already booked");
    }

    private TimetableRequest baseRequest() {
        TimetableRequest request = new TimetableRequest();
        request.setSchoolId(schoolId);
        request.setAcademicYearId(academicYearId);
        request.setClassId(classId);
        request.setSubjectId(subjectId);
        request.setTeacherId(teacherId);
        request.setRoomName("Room 1");
        request.setDayOfWeek("MONDAY");
        request.setStartTime(LocalTime.of(9, 0));
        request.setEndTime(LocalTime.of(10, 0));
        request.setNotes("Morning class");
        return request;
    }
}
