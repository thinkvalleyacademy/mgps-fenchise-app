package com.mgps.school.service;

import com.mgps.common.exception.DuplicateResourceException;
import com.mgps.common.exception.ResourceNotFoundException;
import com.mgps.school.dto.SchoolDomainDTO;
import com.mgps.school.entity.School;
import com.mgps.school.entity.SchoolDomain;
import com.mgps.school.repository.SchoolDomainRepository;
import com.mgps.school.repository.SchoolRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DomainMappingService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DomainMappingService Tests")
class DomainMappingServiceTest {
    
    @Mock
    private SchoolDomainRepository schoolDomainRepository;
    
    @Mock
    private SchoolRepository schoolRepository;
    
    @InjectMocks
    private DomainMappingService domainMappingService;
    
    private UUID schoolId;
    private UUID domainId;
    private School school;
    private SchoolDomain domain;
    
    @BeforeEach
    void setUp() {
        schoolId = UUID.randomUUID();
        domainId = UUID.randomUUID();
        
        school = School.builder()
            .id(schoolId)
            .name("Test School")
            .adminEmail("admin@test.com")
            .build();
        
        domain = SchoolDomain.builder()
            .id(domainId)
            .school(school)
            .domainName("test-school.smsapp.com")
            .isPrimary(true)
            .isActive(true)
            .build();
    }
    
    @Test
    @DisplayName("Should create primary domain successfully")
    void testCreatePrimaryDomainSuccess() {
        // Arrange
        String domainName = "test-school.smsapp.com";
        when(schoolDomainRepository.save(any(SchoolDomain.class))).thenReturn(domain);
        
        // Act
        SchoolDomain result = domainMappingService.createPrimaryDomain(school, domainName);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDomainName()).isEqualTo(domainName);
        assertThat(result.getIsPrimary()).isTrue();
        assertThat(result.getIsActive()).isTrue();
        
        verify(schoolDomainRepository).save(any(SchoolDomain.class));
    }
    
    @Test
    @DisplayName("Should add additional domain successfully")
    void testAddDomainSuccess() {
        // Arrange
        String newDomain = "alternate.smsapp.com";
        SchoolDomain secondaryDomain = SchoolDomain.builder()
            .id(UUID.randomUUID())
            .school(school)
            .domainName(newDomain)
            .isPrimary(false)
            .isActive(true)
            .build();
        
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(school));
        when(schoolDomainRepository.existsByDomainName(newDomain)).thenReturn(false);
        when(schoolDomainRepository.save(any(SchoolDomain.class))).thenReturn(secondaryDomain);
        
        // Act
        SchoolDomain result = domainMappingService.addDomain(schoolId, newDomain);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getDomainName()).isEqualTo(newDomain);
        assertThat(result.getIsPrimary()).isFalse();
        
        verify(schoolRepository).findById(schoolId);
        verify(schoolDomainRepository).existsByDomainName(newDomain);
        verify(schoolDomainRepository).save(any(SchoolDomain.class));
    }
    
    @Test
    @DisplayName("Should throw exception when domain already exists")
    void testAddDomainDuplicate() {
        // Arrange
        String domainName = "test-school.smsapp.com";
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.of(school));
        when(schoolDomainRepository.existsByDomainName(domainName)).thenReturn(true);
        
        // Act & Assert
        assertThatThrownBy(() -> domainMappingService.addDomain(schoolId, domainName))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessage("Domain already exists");
        
        verify(schoolRepository).findById(schoolId);
        verify(schoolDomainRepository, never()).save(any(SchoolDomain.class));
    }
    
    @Test
    @DisplayName("Should throw exception when school not found")
    void testAddDomainSchoolNotFound() {
        // Arrange
        String domainName = "test-school.smsapp.com";
        when(schoolRepository.findById(schoolId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> domainMappingService.addDomain(schoolId, domainName))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("School not found");
        
        verify(schoolRepository).findById(schoolId);
        verify(schoolDomainRepository, never()).save(any(SchoolDomain.class));
    }
    
    @Test
    @DisplayName("Should get school domains")
    void testGetSchoolDomains() {
        // Arrange
        List<SchoolDomain> domains = new ArrayList<>();
        domains.add(domain);
        
        when(schoolDomainRepository.findBySchoolId(schoolId)).thenReturn(domains);
        
        // Act
        List<SchoolDomainDTO> result = domainMappingService.getSchoolDomains(schoolId);
        
        // Assert
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDomainName()).isEqualTo("test-school.smsapp.com");
        
        verify(schoolDomainRepository).findBySchoolId(schoolId);
    }
    
    @Test
    @DisplayName("Should get primary domain")
    void testGetPrimaryDomain() {
        // Arrange
        when(schoolDomainRepository.findBySchoolIdAndIsPrimaryTrue(schoolId))
            .thenReturn(Optional.of(domain));
        
        // Act
        SchoolDomain result = domainMappingService.getPrimaryDomain(schoolId);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIsPrimary()).isTrue();
        
        verify(schoolDomainRepository).findBySchoolIdAndIsPrimaryTrue(schoolId);
    }
    
    @Test
    @DisplayName("Should deactivate domain")
    void testDeactivateDomain() {
        // Arrange
        SchoolDomain inactiveDomain = SchoolDomain.builder()
            .id(domainId)
            .school(school)
            .domainName("test-school.smsapp.com")
            .isPrimary(true)
            .isActive(false)
            .build();
        
        when(schoolDomainRepository.findById(domainId)).thenReturn(Optional.of(domain));
        when(schoolDomainRepository.save(any(SchoolDomain.class))).thenReturn(inactiveDomain);
        
        // Act
        SchoolDomain result = domainMappingService.deactivateDomain(domainId);
        
        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getIsActive()).isFalse();
        
        verify(schoolDomainRepository).findById(domainId);
        verify(schoolDomainRepository).save(any(SchoolDomain.class));
    }
}
