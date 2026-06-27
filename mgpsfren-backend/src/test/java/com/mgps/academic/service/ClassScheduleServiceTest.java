package com.mgps.academic.service;

import com.mgps.academic.dto.DuplicateScheduleRequest;
import com.mgps.academic.entity.ClassSchedule;
import com.mgps.academic.entity.ClassSchedulePeriod;
import com.mgps.academic.entity.ScheduleType;
import com.mgps.academic.repository.ClassSchedulePeriodRepository;
import com.mgps.academic.repository.ClassScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClassScheduleServiceTest {

    @Mock
    private ClassScheduleRepository repository;

    @Mock
    private ClassSchedulePeriodRepository periodRepository;

    @InjectMocks
    private ClassScheduleService service;

    @Test
    void duplicateScheduleCopiesSlotsToRequestedTargetWeek() {
        ClassSchedule source = new ClassSchedule();
        source.setClassName("Grade 1");
        source.setAcademicSession("2025-2026");
        source.setWeekNumber(3);
        source.setDayOfWeek(DayOfWeek.MONDAY);
        source.setPeriodName("P1");
        source.setStartTime(LocalTime.of(9, 0));
        source.setEndTime(LocalTime.of(10, 0));
        source.setScheduleType(ScheduleType.CORE);
        source.setSubject("Mathematics");

        ClassSchedulePeriod period = new ClassSchedulePeriod();
        period.setClassName("Grade 2");
        period.setAcademicSession("2025-2026");
        period.setPeriodName("P1");
        period.setStartTime(LocalTime.of(9, 0));
        period.setEndTime(LocalTime.of(10, 0));

        when(repository.findByClassNameAndAcademicSessionAndWeekNumber("Grade 1", "2025-2026", 3))
                .thenReturn(List.of(source));
        when(periodRepository.findByClassNameAndAcademicSessionAndPeriodName("Grade 2", "2025-2026", "P1"))
                .thenReturn(Optional.of(period));

        DuplicateScheduleRequest request = new DuplicateScheduleRequest();
        request.setSourceClassName("Grade 1");
        request.setSourceSession("2025-2026");
        request.setSourceWeekNumber(3);
        request.setTargetClassName("Grade 2");
        request.setTargetSession("2025-2026");
        request.setTargetWeekNumber(7);

        service.duplicateSchedule(request);

        ArgumentCaptor<List<ClassSchedule>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        List<ClassSchedule> duplicatedSchedules = captor.getValue();
        assertEquals(1, duplicatedSchedules.size());
        assertEquals("Grade 2", duplicatedSchedules.get(0).getClassName());
        assertEquals("2025-2026", duplicatedSchedules.get(0).getAcademicSession());
        assertEquals(7, duplicatedSchedules.get(0).getWeekNumber());
        assertEquals("P1", duplicatedSchedules.get(0).getPeriodName());
    }

    @Test
    void duplicateScheduleOverwritesExistingTargetWeekSlots() {
        ClassSchedule existingTarget = new ClassSchedule();
        existingTarget.setClassName("Grade 2");
        existingTarget.setAcademicSession("2025-2026");
        existingTarget.setWeekNumber(7);
        existingTarget.setDayOfWeek(DayOfWeek.MONDAY);
        existingTarget.setPeriodName("P1");
        existingTarget.setStartTime(LocalTime.of(8, 0));
        existingTarget.setEndTime(LocalTime.of(9, 0));
        existingTarget.setScheduleType(ScheduleType.ACTIVITY);
        existingTarget.setSubject("Circle Time");

        ClassSchedule source = new ClassSchedule();
        source.setClassName("Grade 1");
        source.setAcademicSession("2025-2026");
        source.setWeekNumber(3);
        source.setDayOfWeek(DayOfWeek.MONDAY);
        source.setPeriodName("P1");
        source.setStartTime(LocalTime.of(9, 0));
        source.setEndTime(LocalTime.of(10, 0));
        source.setScheduleType(ScheduleType.CORE);
        source.setSubject("Mathematics");

        ClassSchedulePeriod period = new ClassSchedulePeriod();
        period.setClassName("Grade 2");
        period.setAcademicSession("2025-2026");
        period.setPeriodName("P1");
        period.setStartTime(LocalTime.of(9, 0));
        period.setEndTime(LocalTime.of(10, 0));

        when(repository.findByClassNameAndAcademicSessionAndWeekNumber("Grade 1", "2025-2026", 3))
                .thenReturn(List.of(source));
        when(repository.findByClassNameAndAcademicSessionAndWeekNumber("Grade 2", "2025-2026", 7))
                .thenReturn(List.of(existingTarget));
        when(periodRepository.findByClassNameAndAcademicSessionAndPeriodName("Grade 2", "2025-2026", "P1"))
                .thenReturn(Optional.of(period));

        DuplicateScheduleRequest request = new DuplicateScheduleRequest();
        request.setSourceClassName("Grade 1");
        request.setSourceSession("2025-2026");
        request.setSourceWeekNumber(3);
        request.setTargetClassName("Grade 2");
        request.setTargetSession("2025-2026");
        request.setTargetWeekNumber(7);

        service.duplicateSchedule(request);

        verify(repository).deleteAll(List.of(existingTarget));
        ArgumentCaptor<List<ClassSchedule>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());

        List<ClassSchedule> duplicatedSchedules = captor.getValue();
        assertEquals(1, duplicatedSchedules.size());
        assertEquals("Grade 2", duplicatedSchedules.get(0).getClassName());
        assertEquals(7, duplicatedSchedules.get(0).getWeekNumber());
        assertEquals("P1", duplicatedSchedules.get(0).getPeriodName());
    }
}
