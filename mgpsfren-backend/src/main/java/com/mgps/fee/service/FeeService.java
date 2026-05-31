package com.mgps.fee.service;

import com.mgps.academic.entity.AcademicYear;
import com.mgps.academic.repository.AcademicClassRepository;
import com.mgps.academic.repository.AcademicYearRepository;
import com.mgps.fee.dto.FeeCategoryDTO;
import com.mgps.fee.dto.FeePaymentDTO;
import com.mgps.fee.dto.FeeReportDTOs.*;
import com.mgps.fee.dto.FeeStructureDTO;
import com.mgps.fee.dto.StudentFeeDTO;
import com.mgps.fee.entity.FeeCategory;
import com.mgps.fee.entity.FeePayment;
import com.mgps.fee.entity.FeeStatus;
import com.mgps.fee.entity.FeeStructure;
import com.mgps.fee.entity.StudentFee;
import com.mgps.fee.repository.FeeCategoryRepository;
import com.mgps.fee.repository.FeePaymentRepository;
import com.mgps.fee.repository.FeeStructureRepository;
import com.mgps.fee.repository.StudentFeeRepository;
import com.mgps.student.entity.Student;
import com.mgps.student.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeeService {

    @Autowired
    private FeeCategoryRepository categoryRepository;

    @Autowired
    private FeeStructureRepository structureRepository;

    @Autowired
    private StudentFeeRepository studentFeeRepository;

    @Autowired
    private FeePaymentRepository paymentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AcademicClassRepository classRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    // --- Fee Category Operations ---

    public FeeCategoryDTO createCategory(FeeCategoryDTO dto) {
        FeeCategory category = new FeeCategory();
        category.setId(UUID.randomUUID());
        category.setSchoolId(dto.getSchoolId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setActive(true);
        
        FeeCategory saved = categoryRepository.save(category);
        return mapToDTO(saved);
    }

    public List<FeeCategoryDTO> getCategories(UUID schoolId) {
        return categoryRepository.findBySchoolId(schoolId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- Fee Structure Operations ---

    public FeeStructureDTO createStructure(FeeStructureDTO dto) {
        FeeCategory category = categoryRepository.findById(dto.getFeeCategoryId())
                .orElseThrow(() -> new RuntimeException("Fee Category not found"));

        FeeStructure structure = new FeeStructure();
        structure.setId(UUID.randomUUID());
        structure.setSchoolId(dto.getSchoolId());
        structure.setAcademicYearId(dto.getAcademicYearId());
        structure.setClassId(dto.getClassId());
        structure.setCategory(category);
        structure.setAmount(dto.getAmount());
        structure.setDueDate(dto.getDueDate());
        structure.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
        structure.setRecurrenceType(dto.getRecurrenceType() != null ? dto.getRecurrenceType() : "ONE_TIME");
        structure.setActive(true);

        FeeStructure saved = structureRepository.save(structure);
        return mapToDTO(saved);
    }

    public List<FeeStructureDTO> getStructures(UUID schoolId, UUID academicYearId) {
        return structureRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<FeeStructure> getDefaultStructures(UUID schoolId, UUID academicYearId, UUID classId) {
        // Return active default structures for the school/year. 
        // Optionally filter by class if the structure is class-specific or generic (null classId).
        return structureRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId).stream()
                .filter(s -> Boolean.TRUE.equals(s.getIsDefault()) && Boolean.TRUE.equals(s.getActive()))
                .filter(s -> s.getClassId() == null || s.getClassId().equals(classId))
                .collect(Collectors.toList());
    }

    // --- Student Fee Operations ---

    public StudentFeeDTO assignFeeToStudent(UUID studentId, UUID structureId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        FeeStructure structure = structureRepository.findById(structureId)
                .orElseThrow(() -> new RuntimeException("Fee Structure not found"));

        // Avoid duplicate assignment
        if (studentFeeRepository.findByStudentId(studentId).stream()
                .anyMatch(sf -> sf.getFeeStructure().getId().equals(structureId))) {
            return null; 
        }

        StudentFee studentFee = new StudentFee();
        studentFee.setId(UUID.randomUUID());
        studentFee.setSchoolId(student.getSchoolId());
        studentFee.setStudent(student);
        studentFee.setFeeStructure(structure);
        studentFee.setAmountDue(structure.getAmount());
        studentFee.setAmountPaid(BigDecimal.ZERO);
        studentFee.setDiscountAmount(BigDecimal.ZERO);
        studentFee.setStatus(FeeStatus.UNPAID);
        studentFee.setDueDate(structure.getDueDate());

        StudentFee saved = studentFeeRepository.save(studentFee);
        return mapToDTO(saved);
    }

    public void applyDiscount(UUID studentFeeId, BigDecimal discountAmount, String reason) {
        StudentFee studentFee = studentFeeRepository.findById(studentFeeId)
                .orElseThrow(() -> new RuntimeException("Student Fee record not found"));
        
        studentFee.setDiscountAmount(discountAmount);
        studentFee.setDiscountReason(reason);
        
        // Recalculate status
        BigDecimal effectiveDue = calculateTotalDueTillDate(studentFee).subtract(discountAmount);
        if (studentFee.getAmountPaid().compareTo(effectiveDue) >= 0) {
            studentFee.setStatus(FeeStatus.PAID);
        } else if (studentFee.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            studentFee.setStatus(FeeStatus.PARTIAL);
        }
        
        studentFeeRepository.save(studentFee);
    }

    public List<StudentFeeDTO> getStudentFees(UUID studentId) {
        return studentFeeRepository.findByStudentId(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- Reporting Operations ---

    public SchoolFeeReport getSchoolOverallReport(UUID schoolId, UUID academicYearId) {
        List<StudentFee> allFees = studentFeeRepository.findBySchoolId(schoolId); // Need a better way to filter by year if many
        // For simplicity, let's assume we filter manually or use a custom query
        
        BigDecimal expected = allFees.stream().map(this::calculateTotalDueTillDate).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paid = allFees.stream().map(StudentFee::getAmountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discounted = allFees.stream().map(StudentFee::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        
        SchoolFeeReport report = new SchoolFeeReport();
        report.setSchoolId(schoolId);
        report.setOverallExpected(expected);
        report.setOverallCollected(paid);
        report.setOverallDiscounted(discounted);
        report.setOverallOutstanding(expected.subtract(paid).subtract(discounted));
        report.setActiveStudentsCount((int) allFees.stream().map(sf -> sf.getStudent().getId()).distinct().count());
        
        return report;
    }

    public List<ClassFeeReport> getClassWiseReport(UUID schoolId, UUID academicYearId) {
        // Fetch all classes for the school
        return classRepository.findBySchoolId(schoolId).stream().map(c -> {
            List<StudentFee> classFees = studentFeeRepository.findBySchoolId(schoolId).stream()
                    .filter(sf -> sf.getStudent().getClassId().equals(c.getId()))
                    .collect(Collectors.toList());
            
            BigDecimal expected = classFees.stream().map(this::calculateTotalDueTillDate).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal paid = classFees.stream().map(StudentFee::getAmountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal discounted = classFees.stream().map(StudentFee::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            ClassFeeReport report = new ClassFeeReport();
            report.setClassId(c.getId());
            report.setClassName(c.getName());
            report.setStudentCount((int) classFees.stream().map(sf -> sf.getStudent().getId()).distinct().count());
            report.setTotalExpected(expected);
            report.setTotalCollected(paid);
            report.setTotalDiscounted(discounted);
            report.setTotalOutstanding(expected.subtract(paid).subtract(discounted));
            return report;
        }).collect(Collectors.toList());
    }

    public List<StudentFeeReport> getStudentWiseReport(UUID classId) {
        return studentRepository.findByClassId(classId).stream().map(s -> {
            List<StudentFee> fees = studentFeeRepository.findByStudentId(s.getId());
            
            BigDecimal expected = fees.stream().map(this::calculateTotalDueTillDate).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal paid = fees.stream().map(StudentFee::getAmountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal discounted = fees.stream().map(StudentFee::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

            StudentFeeReport report = new StudentFeeReport();
            report.setStudentId(s.getId());
            report.setStudentName(s.getFirstName() + " " + s.getLastName());
            report.setAdmissionNumber(s.getAdmissionNumber());
            report.setTotalExpected(expected);
            report.setTotalCollected(paid);
            report.setTotalDiscounted(discounted);
            report.setTotalOutstanding(expected.subtract(paid).subtract(discounted));
            
            BigDecimal balance = expected.subtract(paid).subtract(discounted);
            if (balance.compareTo(BigDecimal.ZERO) <= 0 && expected.compareTo(BigDecimal.ZERO) > 0) {
                report.setStatus("PAID");
            } else if (paid.compareTo(BigDecimal.ZERO) > 0) {
                report.setStatus("PARTIAL");
            } else {
                report.setStatus("UNPAID");
            }
            
            return report;
        }).collect(Collectors.toList());
    }

    // --- Payment Operations ---

    public FeePaymentDTO processPayment(FeePaymentDTO dto) {
        StudentFee studentFee = studentFeeRepository.findById(dto.getStudentFeeId())
                .orElseThrow(() -> new RuntimeException("Student Fee record not found"));

        FeePayment payment = new FeePayment();
        payment.setId(UUID.randomUUID());
        payment.setSchoolId(studentFee.getSchoolId());
        payment.setStudentFee(studentFee);
        payment.setAmountPaid(dto.getAmountPaid());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMode(dto.getPaymentMode());
        payment.setTransactionId(dto.getTransactionId());
        payment.setReceiptNumber(dto.getReceiptNumber() != null ? dto.getReceiptNumber() : "REC-" + System.currentTimeMillis());
        payment.setRemarks(dto.getRemarks());
        payment.setProcessedBy(dto.getProcessedBy());
        payment.setMonthFrom(dto.getMonthFrom());
        payment.setMonthTo(dto.getMonthTo());

        FeePayment saved = paymentRepository.save(payment);

        // Update StudentFee status and paid amount
        studentFee.setAmountPaid(studentFee.getAmountPaid().add(dto.getAmountPaid()));
        
        BigDecimal effectiveDue = calculateTotalDueTillDate(studentFee).subtract(studentFee.getDiscountAmount());
        if (studentFee.getAmountPaid().compareTo(effectiveDue) >= 0) {
            studentFee.setStatus(FeeStatus.PAID);
        } else if (studentFee.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            studentFee.setStatus(FeeStatus.PARTIAL);
        }
        studentFeeRepository.save(studentFee);

        return mapToDTO(saved);
    }

    public List<FeePaymentDTO> getRecentPayments(UUID schoolId) {
        return paymentRepository.findBySchoolId(schoolId).stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(10)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- Calculation Helpers ---

    private BigDecimal calculateTotalDueTillDate(StudentFee sf) {
        if (sf.getFeeStructure().getRecurrenceType().equals("ONE_TIME")) {
            return sf.getAmountDue();
        }

        // Monthly fee logic
        AcademicYear year = academicYearRepository.findById(sf.getFeeStructure().getAcademicYearId())
                .orElseThrow(() -> new RuntimeException("Academic Year not found"));
        
        LocalDate sessionStart = year.getStartDate();
        LocalDate today = LocalDate.now();
        
        if (today.isBefore(sessionStart)) {
            return BigDecimal.ZERO;
        }
        
        // Months elapsed (inclusive of current month)
        Period period = Period.between(sessionStart.withDayOfMonth(1), today.withDayOfMonth(1));
        int monthsElapsed = period.getYears() * 12 + period.getMonths() + 1;
        
        return sf.getAmountDue().multiply(new BigDecimal(monthsElapsed));
    }

    // --- Mapping Helpers ---

    private FeeCategoryDTO mapToDTO(FeeCategory entity) {
        FeeCategoryDTO dto = new FeeCategoryDTO();
        dto.setId(entity.getId());
        dto.setSchoolId(entity.getSchoolId());
        dto.setName(entity.getName());
        dto.setDescription(entity.getDescription());
        dto.setActive(entity.getActive());
        return dto;
    }

    private FeeStructureDTO mapToDTO(FeeStructure entity) {
        FeeStructureDTO dto = new FeeStructureDTO();
        dto.setId(entity.getId());
        dto.setSchoolId(entity.getSchoolId());
        dto.setAcademicYearId(entity.getAcademicYearId());
        dto.setClassId(entity.getClassId());
        dto.setFeeCategoryId(entity.getCategory().getId());
        dto.setFeeCategoryName(entity.getCategory().getName());
        dto.setAmount(entity.getAmount());
        dto.setDueDate(entity.getDueDate());
        dto.setIsDefault(entity.getIsDefault());
        dto.setActive(entity.getActive());
        dto.setRecurrenceType(entity.getRecurrenceType());
        return dto;
    }

    private StudentFeeDTO mapToDTO(StudentFee entity) {
        StudentFeeDTO dto = new StudentFeeDTO();
        dto.setId(entity.getId());
        dto.setSchoolId(entity.getSchoolId());
        dto.setStudentId(entity.getStudent().getId());
        dto.setStudentName(entity.getStudent().getFirstName() + " " + entity.getStudent().getLastName());
        dto.setFeeStructureId(entity.getFeeStructure().getId());
        dto.setFeeCategoryName(entity.getFeeStructure().getCategory().getName());
        dto.setAmountDue(entity.getAmountDue());
        dto.setAmountPaid(entity.getAmountPaid());
        dto.setStatus(entity.getStatus().name());
        dto.setDueDate(entity.getDueDate());
        dto.setDiscountAmount(entity.getDiscountAmount());
        dto.setDiscountReason(entity.getDiscountReason());
        dto.setRecurrenceType(entity.getFeeStructure().getRecurrenceType());
        dto.setTotalDueTillDate(calculateTotalDueTillDate(entity));
        return dto;
    }

    private FeePaymentDTO mapToDTO(FeePayment entity) {
        FeePaymentDTO dto = new FeePaymentDTO();
        dto.setId(entity.getId());
        dto.setSchoolId(entity.getSchoolId());
        dto.setStudentFeeId(entity.getStudentFee().getId());
        dto.setAmountPaid(entity.getAmountPaid());
        dto.setPaymentDate(entity.getPaymentDate());
        dto.setPaymentMode(entity.getPaymentMode());
        dto.setTransactionId(entity.getTransactionId());
        dto.setReceiptNumber(entity.getReceiptNumber());
        dto.setRemarks(entity.getRemarks());
        dto.setProcessedBy(entity.getProcessedBy());
        dto.setMonthFrom(entity.getMonthFrom());
        dto.setMonthTo(entity.getMonthTo());
        return dto;
    }
}
