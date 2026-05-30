package com.mgps.school.service;

import com.mgps.common.exception.DuplicateResourceException;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.school.dto.SchoolCreatedDTO;
import com.mgps.school.dto.SchoolRegistrationDTO;
import com.mgps.school.dto.SchoolDTO;
import com.mgps.school.dto.SchoolUpdateDTO;
import com.mgps.school.entity.School;
import com.mgps.school.entity.SchoolDomain;
import com.mgps.school.entity.SchoolStatus;
import com.mgps.school.entity.SubscriptionPlan;
import com.mgps.school.exception.DatabaseProvisioningException;
import com.mgps.school.repository.SchoolDomainRepository;
import com.mgps.school.repository.SchoolRepository;
import com.mgps.school.repository.SubscriptionPlanRepository;
import com.mgps.tenant.RoutingDataSource;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SchoolService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SchoolService Tests")
class SchoolServiceTest {
    
    @Mock
    private SchoolRepository schoolRepository;
    
    @Mock
    private SchoolDomainRepository schoolDomainRepository;
    
    @Mock
    private SubscriptionPlanRepository subscriptionPlanRepository;
    
    @Mock
    private DatabaseProvisioningService databaseProvisioningService;
    
    @Mock
    private DomainMappingService domainMappingService;
    
    @Mock
    private RoutingDataSource routingDataSource;
    
    @InjectMocks
    private SchoolService schoolService;
    
    private SchoolRegistrationDTO registrationDTO;
    private SubscriptionPlan subscriptionPlan;
    private School school;
    private UUID schoolId;
    
    @BeforeEach
    void setUp() {
        schoolId = UUID.randomUUID();
        
        // Setup subscription plan
        subscriptionPlan = SubscriptionPlan.builder()
            .id(UUID.randomUUID())
            .planName("Standard")
            .maxStudents(500)
            .maxStaff(50)
            .maxUsers(550)
            .monthlyPrice(new BigDecimal("5000"))
            .isActive(true)
            .build();
        
        // Setup registration DTO
        registrationDTO = SchoolRegistrationDTO.builder()
            .name("Test School")
            .adminEmail("admin@testschool.com")
            .adminPhone("+91-1234567890")
            .address("123 Test Street")
            .city("Bangalore")
            .state("Karnataka")
            .postalCode("560001")
            .subscriptionPlanId(subscriptionPlan.getId())
            .build();
        
        // Setup school entity
        school = School.builder()
            .id(schoolId)
            .name(registrationDTO.getName())
            .adminEmail(registrationDTO.getAdminEmail())
            .adminPhone(registrationDTO.getAdminPhone())
            .address(registrationDTO.getAddress())
            .city(registrationDTO.getCity())
            .state(registrationDTO.getState())
            .postalCode(registrationDTO.getPostalCode())
            .subscriptionPlan(subscriptionPlan)
            .status(SchoolStatus.ACTIVE)
            .databaseName("school_testdb_db")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
    
    @Test
    @DisplayName("Should register school successfully")
    void testRegisterSchoolSuccess() {
        // Arrange
        when(schoolRepository.existsByAdminEmail(registrationDTO.getAdminEmail())).thenReturn(false);
        when(subscriptionPlanRepository.findById(registrationDTO.getSubscriptionPlanId()))
            .thenReturn(Optional.of(subscriptionPlan));
        when(schoolRepository.save(any(School.class))).thenReturn(school);
        when(domainMappingService.createPrimaryDomain(any(School.class), anyString()))
            .thenReturn(SchoolDomain.builder()
                .id(UUID.randomUUID())
                .domainName("test-school.smsapp.com")
                .isPrimary(true)
                .isActive(true)
                .build());
        
        // Act
        SchoolCreatedDTO result = schoolService.registerSchool(registrationDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSchoolId()).isEqualTo(schoolId);
        assertThat(result.getName()).isEqualTo(registrationDTO.getName());
        assertThat(result.getStatus()).isEqualTo(SchoolStatus.ACTIVE);
        assertThat(result.getAdminUrl()).contains("smsapp.com");
        
        verify(schoolRepository).existsByAdminEmail(registrationDTO.getAdminEmail());
        verify(subscriptionPlanRepository).findById(registrationDTO.getSubscriptionPlanId());
        verify(schoolRepository).save(any(School.class));
        verify(databaseProvisioningService).provisionDatabase(any(School.class));
    }
    
    @Test
    @DisplayName("Should throw exception when email already exists")
    void testRegisterSchoolDuplicateEmail() {
        // Arrange
        when(schoolRepository.existsByAdminEmail(registrationDTO.getAdminEmail())).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> schoolService.registerSchool(registrationDTO))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessage("School with this email already exists");
        
        verify(schoolRepository).existsByAdminEmail(registrationDTO.getAdminEmail());
        verify(schoolRepository, never()).save(any(School.class));
    }
    
    @Test
    @DisplayName("Should throw exception when subscription plan not found")
    void testRegisterSchoolInvalidPlan() {
        // Arrange
        when(schoolRepository.existsByAdminEmail(registrationDTO.getAdminEmail())).thenReturn(false);
        when(subscriptionPlanRepository.findById(registrationDTO.getSubscriptionPlanId()))
            .thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> schoolService.registerSchool(registrationDTO))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("Subscription plan not found");
        
        verify(schoolRepository, never()).save(any(School.class));
    }
    
    @Test
    @DisplayName("Should rollback school creation when database provisioning fails")
    void testRegisterSchoolProvisioningFailure() {
        // Arrange
        when(schoolRepository.existsByAdminEmail(registrationDTO.getAdminEmail())).thenReturn(false);
        when(subscriptionPlanRepository.findById(registrationDTO.getSubscriptionPlanId()))
            .thenReturn(Optional.of(subscriptionPlan));
        when(schoolRepository.save(any(School.class))).thenReturn(school);
        doThrow(new RuntimeException("Database creation failed"))
            .when(databaseProvisioningService).provisionDatabase(any(School.class));
        
        // Act & Assert
        assertThatThrownBy(() -> schoolService.registerSchool(registrationDTO))
            .isInstanceOf(DatabaseProvisioningException.class);
        
        verify(schoolRepository).delete(school);
    }
    
    @Test
    @DisplayName("Should get school by ID")
    void testGetSchoolById() {
        // Arrange
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(school));
        
        // Act
        SchoolDTO result = schoolService.getSchoolById(schoolId);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getSchoolId()).isEqualTo(schoolId);
        assertThat(result.getName()).isEqualTo(school.getName());
        assertThat(result.getAdminEmail()).isEqualTo(school.getAdminEmail());
        
        verify(schoolRepository).findById(schoolId);
    }
    
    @Test
    @DisplayName("Should throw exception when school not found")
    void testGetSchoolByIdNotFound() {
        // Arrange
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> schoolService.getSchoolById(schoolId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("School not found");
        
        verify(schoolRepository).findById(schoolId);
    }
    
    @Test
    @DisplayName("Should update school successfully")
    void testUpdateSchoolSuccess() {
        // Arrange
        SchoolUpdateDTO updateDTO = SchoolUpdateDTO.builder()
            .name("Updated School Name")
            .city("Mumbai")
            .build();
        
        School updatedSchool = School.builder()
            .id(schoolId)
            .name("Updated School Name")
            .adminEmail(school.getAdminEmail())
            .city("Mumbai")
            .status(SchoolStatus.ACTIVE)
            .createdAt(school.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();
        
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(school));
        when(schoolRepository.save(any(School.class))).thenReturn(updatedSchool);
        
        // Act
        SchoolDTO result = schoolService.updateSchool(schoolId, updateDTO);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Updated School Name");
        assertThat(result.getCity()).isEqualTo("Mumbai");
        
        verify(schoolRepository).findById(schoolId);
        verify(schoolRepository).save(any(School.class));
    }
    
    @Test
    @DisplayName("Should change school status")
    void testChangeSchoolStatus() {
        // Arrange
        School suspendedSchool = School.builder()
            .id(schoolId)
            .name(school.getName())
            .status(SchoolStatus.SUSPENDED)
            .build();
        
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(school));
        when(schoolRepository.save(any(School.class))).thenReturn(suspendedSchool);
        
        // Act
        SchoolDTO result = schoolService.changeStatus(schoolId, SchoolStatus.SUSPENDED);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(SchoolStatus.SUSPENDED);
        
        verify(schoolRepository).findById(schoolId);
        verify(schoolRepository).save(any(School.class));
    }
}
