package com.mgps.academic.repository;

import com.mgps.academic.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, UUID> {
    List<TimetableEntry> findBySchoolIdAndAcademicYearId(UUID schoolId, UUID academicYearId);
    List<TimetableEntry> findBySchoolIdAndAcademicYearIdAndDayOfWeek(UUID schoolId, UUID academicYearId, String dayOfWeek);
    List<TimetableEntry> findBySchoolIdAndAcademicYearIdAndClassId(UUID schoolId, UUID academicYearId, UUID classId);
    List<TimetableEntry> findBySchoolIdAndAcademicYearIdAndTeacherIdAndDayOfWeek(UUID schoolId, UUID academicYearId, UUID teacherId, String dayOfWeek);
    List<TimetableEntry> findBySchoolIdAndAcademicYearIdAndRoomNameAndDayOfWeek(UUID schoolId, UUID academicYearId, String roomName, String dayOfWeek);
}
