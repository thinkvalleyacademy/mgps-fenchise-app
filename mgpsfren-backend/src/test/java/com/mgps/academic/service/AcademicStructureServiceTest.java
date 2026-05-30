package com.mgps.academic.service;

import com.mgps.academic.dto.AcademicDtos.AcademicClassRequest;
import com.mgps.academic.dto.AcademicDtos.AcademicYearRequest;
import com.mgps.academic.dto.AcademicDtos.AcademicSubjectRequest;
import com.mgps.academic.entity.AcademicClass;
import com.mgps.academic.entity.AcademicYear;
import com.mgps.academic.entity.AcademicSubject;
import com.mgps.academic.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AcademicStructureServiceTest {

    @Mock private AcademicYearRepository academicYearRepository;
    @Mock private AcademicClassRepository academicClassRepository;
    @Mock private AcademicSectionRepository academicSectionRepository;
    @Mock private AcademicStreamRepository academicStreamRepository;
    @Mock private AcademicSubjectRepository academicSubjectRepository;
    @Mock private AcademicDepartmentRepository academicDepartmentRepository;
    @Mock private AcademicHouseRepository academicHouseRepository;

    @InjectMocks
    private AcademicStructureService service;

    private UUID schoolId;

    @BeforeEach
    void setUp() {
        schoolId = UUID.randomUUID();
    }

    @Test
    void shouldCreateAcademicYear() {
        AcademicYearRequest request = new AcademicYearRequest();
        request.setSchoolId(schoolId);
        request.setName("2026-2027");
        request.setStartDate(LocalDate.of(2026, 6, 1));
        request.setEndDate(LocalDate.of(2027, 5, 31));

        AcademicYear saved = new AcademicYear(UUID.randomUUID(), schoolId, "2026-2027",
            LocalDate.of(2026, 6, 1), LocalDate.of(2027, 5, 31), false, null, null);

        when(academicYearRepository.save(any(AcademicYear.class))).thenReturn(saved);

        var response = service.createAcademicYear(request);

        assertThat(response.getSchoolId()).isEqualTo(schoolId);
        assertThat(response.getName()).isEqualTo("2026-2027");
        assertThat(response.getIsActive()).isFalse();
    }

    @Test
    void shouldCreateAcademicClass() {
        AcademicClassRequest request = new AcademicClassRequest();
        request.setSchoolId(schoolId);
        request.setAcademicYearId(UUID.randomUUID());
        request.setName("Class 10");
        request.setGradeLevel("10");
        request.setDescription("Secondary class");

        AcademicClass saved = new AcademicClass(UUID.randomUUID(), schoolId, request.getAcademicYearId(),
            "Class 10", "10", "Secondary class", true, null, null);

        when(academicClassRepository.save(any(AcademicClass.class))).thenReturn(saved);

        var response = service.createAcademicClass(request);

        assertThat(response.getSchoolId()).isEqualTo(schoolId);
        assertThat(response.getName()).isEqualTo("Class 10");
        assertThat(response.getGradeLevel()).isEqualTo("10");
    }

    @Test
    void shouldCreateAcademicSubject() {
        AcademicSubjectRequest request = new AcademicSubjectRequest();
        request.setSchoolId(schoolId);
        request.setClassId(UUID.randomUUID());
        request.setName("Mathematics");
        request.setCode("MATH");
        request.setDescription("Core subject");

        AcademicSubject saved = new AcademicSubject(UUID.randomUUID(), schoolId, request.getClassId(),
            "Mathematics", "MATH", "Core subject", true, null, null);

        when(academicSubjectRepository.save(any(AcademicSubject.class))).thenReturn(saved);

        var response = service.createSubject(request);

        assertThat(response.getSchoolId()).isEqualTo(schoolId);
        assertThat(response.getName()).isEqualTo("Mathematics");
    }
}
