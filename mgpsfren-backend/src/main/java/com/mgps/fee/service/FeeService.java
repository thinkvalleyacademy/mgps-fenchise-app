package com.mgps.fee.service;

import com.mgps.fee.dto.FeeCategoryDTO;
import com.mgps.fee.dto.FeePaymentDTO;
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
import java.time.LocalDateTime;
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
        structure.setActive(true);

        FeeStructure saved = structureRepository.save(structure);
        return mapToDTO(saved);
    }

    public List<FeeStructureDTO> getStructures(UUID schoolId, UUID academicYearId) {
        return structureRepository.findBySchoolIdAndAcademicYearId(schoolId, academicYearId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- Student Fee Operations ---

    public StudentFeeDTO assignFeeToStudent(UUID studentId, UUID structureId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        FeeStructure structure = structureRepository.findById(structureId)
                .orElseThrow(() -> new RuntimeException("Fee Structure not found"));

        StudentFee studentFee = new StudentFee();
        studentFee.setId(UUID.randomUUID());
        studentFee.setSchoolId(student.getSchoolId());
        studentFee.setStudent(student);
        studentFee.setFeeStructure(structure);
        studentFee.setAmountDue(structure.getAmount());
        studentFee.setAmountPaid(BigDecimal.ZERO);
        studentFee.setStatus(FeeStatus.UNPAID);
        studentFee.setDueDate(structure.getDueDate());

        StudentFee saved = studentFeeRepository.save(studentFee);
        return mapToDTO(saved);
    }

    public List<StudentFeeDTO> getStudentFees(UUID studentId) {
        return studentFeeRepository.findByStudentId(studentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
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

        FeePayment saved = paymentRepository.save(payment);

        // Update StudentFee status and paid amount
        studentFee.setAmountPaid(studentFee.getAmountPaid().add(dto.getAmountPaid()));
        if (studentFee.getAmountPaid().compareTo(studentFee.getAmountDue()) >= 0) {
            studentFee.setStatus(FeeStatus.PAID);
        } else if (studentFee.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            studentFee.setStatus(FeeStatus.PARTIAL);
        }
        studentFeeRepository.save(studentFee);

        return mapToDTO(saved);
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
        dto.setActive(entity.getActive());
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
        return dto;
    }
}
