package com.mgps.fee.service;

import com.mgps.academic.entity.AcademicYear;
import com.mgps.academic.repository.AcademicClassRepository;
import com.mgps.academic.repository.AcademicYearRepository;
import com.mgps.common.exception.BusinessLogicException;
import com.mgps.fee.dto.BulkFeePaymentRequestDTO;
import com.mgps.fee.dto.BulkFeePaymentResultDTO;
import com.mgps.fee.dto.FeeCategoryDTO;
import com.mgps.fee.dto.FeePaymentDTO;
import com.mgps.fee.dto.FeeReportDTOs.*;
import com.mgps.fee.dto.FeeSettingsDTO;
import com.mgps.fee.dto.FeeStructureDTO;
import com.mgps.fee.dto.StudentFeeDTO;
import com.mgps.fee.entity.FeeCategory;
import com.mgps.fee.entity.FeePayment;
import com.mgps.fee.entity.FeeSettings;
import com.mgps.fee.entity.FeeStatus;
import com.mgps.fee.entity.FeeStructure;
import com.mgps.fee.entity.StudentFee;
import com.mgps.fee.repository.FeeCategoryRepository;
import com.mgps.fee.repository.FeePaymentRepository;
import com.mgps.fee.repository.FeeSettingsRepository;
import com.mgps.fee.repository.FeeStructureRepository;
import com.mgps.fee.entity.FineType;
import com.mgps.fee.repository.StudentFeeRepository;
import com.mgps.student.entity.Student;
import com.mgps.student.repository.StudentRepository;
import com.mgps.tenant.TenantGuard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class FeeService {

    private static final String RECURRENCE_ONE_TIME = "ONE_TIME";
    private static final String RECURRENCE_MONTHLY = "MONTHLY";

    @Autowired
    private FeeCategoryRepository categoryRepository;

    @Autowired
    private FeeStructureRepository structureRepository;

    @Autowired
    private StudentFeeRepository studentFeeRepository;

    @Autowired
    private FeePaymentRepository paymentRepository;

    @Autowired
    private FeeSettingsRepository feeSettingsRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private AcademicClassRepository classRepository;

    @Autowired
    private AcademicYearRepository academicYearRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TenantGuard tenantGuard;

    // --- Fee Category Operations ---

    public FeeCategoryDTO createCategory(FeeCategoryDTO dto) {
        tenantGuard.assertSchoolAccessible(dto.getSchoolId());
        categoryRepository.findBySchoolIdAndNameIgnoreCase(dto.getSchoolId(), dto.getName())
                .ifPresent(existing -> {
                    throw new BusinessLogicException("Fee category already exists");
                });

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
        tenantGuard.assertSchoolAccessible(schoolId);
        ensureDefaultCategories(schoolId);
        return categoryRepository.findBySchoolId(schoolId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- Fee Structure Operations ---

    public List<FeeStructureDTO> createStructure(FeeStructureDTO dto) {
        tenantGuard.assertSchoolAccessible(dto.getSchoolId());

        List<UUID> targetClassIds;
        if (Boolean.TRUE.equals(dto.getApplyToAllClasses())) {
            targetClassIds = Collections.singletonList(null);
        } else if (dto.getClassIds() != null && !dto.getClassIds().isEmpty()) {
            targetClassIds = dto.getClassIds();
        } else if (dto.getClassId() != null) {
            targetClassIds = List.of(dto.getClassId());
        } else {
            throw new BusinessLogicException("Target Class is required");
        }

        FeeCategory category = categoryRepository.findById(dto.getFeeCategoryId())
                .orElseThrow(() -> new RuntimeException("Fee Category not found"));
        String recurrenceType = resolveRecurrenceType(dto.getRecurrenceType(), category.getName());
        FineType fineType = resolveFineType(dto.getFineType());
        BigDecimal fineAmount = dto.getFineAmount() != null ? dto.getFineAmount() : BigDecimal.ZERO;
        Integer gracePeriodDays = dto.getGracePeriodDays() != null ? dto.getGracePeriodDays() : 0;

        List<FeeStructureDTO> created = new ArrayList<>();
        for (UUID classId : targetClassIds) {
            FeeStructure structure = new FeeStructure();
            structure.setId(UUID.randomUUID());
            structure.setSchoolId(dto.getSchoolId());
            structure.setAcademicYearId(dto.getAcademicYearId());
            structure.setClassId(classId);
            structure.setCategory(category);
            structure.setAmount(dto.getAmount());
            structure.setDueDate(RECURRENCE_MONTHLY.equals(recurrenceType) ? null : dto.getDueDate());
            structure.setIsDefault(dto.getIsDefault() != null ? dto.getIsDefault() : false);
            structure.setRecurrenceType(recurrenceType);
            structure.setActive(true);
            structure.setFineType(fineType);
            structure.setFineAmount(fineAmount);
            structure.setGracePeriodDays(gracePeriodDays);
            created.add(mapToDTO(structureRepository.save(structure)));
        }
        return created;
    }

    public List<FeeStructureDTO> getStructures(UUID schoolId, UUID academicYearId) {
        tenantGuard.assertSchoolAccessible(schoolId);
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
        studentFee.setDueDate(RECURRENCE_MONTHLY.equals(structure.getRecurrenceType()) ? null : structure.getDueDate());

        StudentFee saved = studentFeeRepository.save(studentFee);
        return mapToDTO(saved);
    }

    public void applyDiscount(UUID studentFeeId, BigDecimal discountAmount, String reason) {
        StudentFee studentFee = studentFeeRepository.findById(studentFeeId)
                .orElseThrow(() -> new RuntimeException("Student Fee record not found"));
        
        studentFee.setDiscountAmount(discountAmount);
        studentFee.setDiscountReason(reason);
        
        // Recalculate status
        updateStatus(studentFee);
        
        studentFeeRepository.save(studentFee);
    }

    public List<StudentFeeDTO> getStudentFees(UUID studentId, UUID academicYearId) {
        return studentFeeRepository.findByStudentId(studentId).stream()
                .filter(sf -> academicYearId == null || academicYearId.equals(sf.getFeeStructure().getAcademicYearId()))
                .peek(this::updateStatus)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public int refreshDueStatuses() {
        List<StudentFee> fees = studentFeeRepository.findAll();
        int updated = 0;
        for (StudentFee fee : fees) {
            FeeStatus previous = fee.getStatus();
            updateStatus(fee);
            if (previous != fee.getStatus()) {
                updated++;
            }
        }
        studentFeeRepository.saveAll(fees);
        return updated;
    }

    // --- Reporting Operations ---

    public SchoolFeeReport getSchoolOverallReport(UUID schoolId, UUID academicYearId) {
        tenantGuard.assertSchoolAccessible(schoolId);
        List<StudentFee> allFees = studentFeeRepository.findBySchoolId(schoolId).stream()
                .filter(sf -> academicYearId.equals(sf.getFeeStructure().getAcademicYearId()))
                .collect(Collectors.toList());
        
        BigDecimal expected = allFees.stream().map(this::calculateTotalDueTillDate).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paid = allFees.stream().map(StudentFee::getAmountPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discounted = allFees.stream().map(StudentFee::getDiscountAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        
        SchoolFeeReport report = new SchoolFeeReport();
        report.setSchoolId(schoolId);
        report.setOverallExpected(expected);
        report.setOverallCollected(paid);
        report.setOverallDiscounted(discounted);
        report.setOverallOutstanding(clampZero(expected.subtract(paid).subtract(discounted)));
        report.setActiveStudentsCount((int) allFees.stream().map(sf -> sf.getStudent().getId()).distinct().count());
        
        return report;
    }

    public List<ClassFeeReport> getClassWiseReport(UUID schoolId, UUID academicYearId) {
        tenantGuard.assertSchoolAccessible(schoolId);
        // Fetch all classes for the school
        return classRepository.findBySchoolId(schoolId).stream().map(c -> {
            List<StudentFee> classFees = studentFeeRepository.findBySchoolId(schoolId).stream()
                    .filter(sf -> sf.getStudent().getClassId().equals(c.getId()))
                    .filter(sf -> academicYearId.equals(sf.getFeeStructure().getAcademicYearId()))
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
            report.setTotalOutstanding(clampZero(expected.subtract(paid).subtract(discounted)));
            return report;
        }).collect(Collectors.toList());
    }

    public List<StudentFeeReport> getStudentWiseReport(UUID classId, UUID academicYearId) {
        return studentRepository.findByClassId(classId).stream().map(s -> {
            List<StudentFee> fees = studentFeeRepository.findByStudentId(s.getId()).stream()
                    .filter(sf -> academicYearId == null || academicYearId.equals(sf.getFeeStructure().getAcademicYearId()))
                    .collect(Collectors.toList());
            
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
            report.setTotalOutstanding(clampZero(expected.subtract(paid).subtract(discounted)));
            
            BigDecimal balance = clampZero(expected.subtract(paid).subtract(discounted));
            if (balance.compareTo(BigDecimal.ZERO) <= 0 && expected.compareTo(BigDecimal.ZERO) > 0) {
                report.setStatus("PAID");
            } else if (expected.compareTo(BigDecimal.ZERO) > 0) {
                report.setStatus("DUE");
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
        return processPaymentInternal(studentFee, dto);
    }

    private FeePaymentDTO processPaymentInternal(StudentFee studentFee, FeePaymentDTO dto) {
        if (dto.getAmountPaid() == null || dto.getAmountPaid().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessLogicException("Payment amount must be greater than zero");
        }

        validateMonthlyPaymentRange(studentFee, dto);

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

        updateStatus(studentFee);
        studentFeeRepository.save(studentFee);

        return mapToDTO(saved);
    }

    /**
     * Collects all of a student's outstanding fee items in one go for the selected
     * period (MONTH/QUARTER/YEAR). A YEAR collection applies the school's configured
     * yearly-payment discount (see FeeSettings), computed server-side.
     */
    public BulkFeePaymentResultDTO processBulkPayment(BulkFeePaymentRequestDTO dto) {
        tenantGuard.assertSchoolAccessible(dto.getSchoolId());
        if (dto.getStudentId() == null) {
            throw new BusinessLogicException("Student is required");
        }
        String collectionType = dto.getCollectionType() == null ? "MONTH" : dto.getCollectionType().toUpperCase();
        if (!List.of("MONTH", "QUARTER", "YEAR").contains(collectionType)) {
            throw new BusinessLogicException("Invalid collection type");
        }

        List<StudentFee> pendingFees = studentFeeRepository.findByStudentId(dto.getStudentId()).stream()
                .filter(sf -> dto.getAcademicYearId() == null || dto.getAcademicYearId().equals(sf.getFeeStructure().getAcademicYearId()))
                .peek(this::updateStatus)
                .filter(sf -> calculateOutstandingBalance(sf).compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        if (pendingFees.isEmpty()) {
            throw new BusinessLogicException("No pending fees to collect for this student");
        }

        BigDecimal discountPercent = "YEAR".equals(collectionType)
                ? getOrCreateSettingsEntity(dto.getSchoolId()).getYearlyDiscountPercent()
                : BigDecimal.ZERO;

        List<FeePaymentDTO> payments = new ArrayList<>();
        BigDecimal totalCollected = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        for (StudentFee sf : pendingFees) {
            Integer monthFrom = null;
            Integer monthTo = null;
            BigDecimal amountBeforeDiscount;

            if (RECURRENCE_MONTHLY.equals(sf.getFeeStructure().getRecurrenceType())) {
                int monthsToCover = resolveMonthsForCollectionType(sf, collectionType);
                if (monthsToCover <= 0) {
                    continue;
                }
                int paidThrough = calculatePaidThroughMonth(sf);
                monthFrom = paidThrough + 1;
                monthTo = Math.min(paidThrough + monthsToCover, calculateTotalMonthsInSession(sf));
                if (monthTo < monthFrom) {
                    continue;
                }
                int coveredMonths = monthTo - monthFrom + 1;
                amountBeforeDiscount = sf.getAmountDue().multiply(BigDecimal.valueOf(coveredMonths));
            } else {
                amountBeforeDiscount = calculateOutstandingBalance(sf);
                if (amountBeforeDiscount.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
            }

            BigDecimal discountForItem = discountPercent.compareTo(BigDecimal.ZERO) > 0
                    ? amountBeforeDiscount.multiply(discountPercent).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;
            BigDecimal amountToCollect = amountBeforeDiscount.subtract(discountForItem);

            if (discountForItem.compareTo(BigDecimal.ZERO) > 0) {
                sf.setDiscountAmount(safeAmount(sf.getDiscountAmount()).add(discountForItem));
                sf.setDiscountReason(appendDiscountReason(sf.getDiscountReason(),
                        "Yearly payment discount (" + discountPercent.stripTrailingZeros().toPlainString() + "%)"));
            }

            FeePaymentDTO paymentDto = new FeePaymentDTO();
            paymentDto.setAmountPaid(amountToCollect);
            paymentDto.setPaymentMode(dto.getPaymentMode());
            paymentDto.setTransactionId(dto.getTransactionId());
            paymentDto.setRemarks(dto.getRemarks());
            paymentDto.setProcessedBy(dto.getProcessedBy());
            paymentDto.setMonthFrom(monthFrom);
            paymentDto.setMonthTo(monthTo);

            payments.add(processPaymentInternal(sf, paymentDto));
            totalCollected = totalCollected.add(amountToCollect);
            totalDiscount = totalDiscount.add(discountForItem);
        }

        if (payments.isEmpty()) {
            throw new BusinessLogicException("No pending fees to collect for the selected period");
        }

        BulkFeePaymentResultDTO result = new BulkFeePaymentResultDTO();
        result.setStudentId(dto.getStudentId());
        result.setCollectionType(collectionType);
        result.setPayments(payments);
        result.setTotalCollected(totalCollected);
        result.setDiscountPercentApplied(discountPercent);
        result.setTotalDiscountApplied(totalDiscount);
        return result;
    }

    private int resolveMonthsForCollectionType(StudentFee sf, String collectionType) {
        int paidThrough = calculatePaidThroughMonth(sf);
        int totalMonths = calculateTotalMonthsInSession(sf);
        int remaining = totalMonths - paidThrough;
        if (remaining <= 0) {
            return 0;
        }
        return switch (collectionType) {
            case "QUARTER" -> Math.min(3, remaining);
            case "YEAR" -> remaining;
            default -> Math.min(1, remaining);
        };
    }

    private String appendDiscountReason(String existing, String addition) {
        if (existing == null || existing.isBlank()) {
            return addition;
        }
        if (existing.contains(addition)) {
            return existing;
        }
        return existing + "; " + addition;
    }

    // --- Fee Settings Operations ---

    public FeeSettingsDTO getSettings(UUID schoolId) {
        tenantGuard.assertSchoolAccessible(schoolId);
        FeeSettings settings = getOrCreateSettingsEntity(schoolId);
        return new FeeSettingsDTO(settings.getSchoolId(), settings.getYearlyDiscountPercent());
    }

    public FeeSettingsDTO updateSettings(UUID schoolId, BigDecimal yearlyDiscountPercent) {
        tenantGuard.assertSchoolAccessible(schoolId);
        if (yearlyDiscountPercent == null
                || yearlyDiscountPercent.compareTo(BigDecimal.ZERO) < 0
                || yearlyDiscountPercent.compareTo(new BigDecimal(100)) > 0) {
            throw new BusinessLogicException("Yearly discount percent must be between 0 and 100");
        }
        FeeSettings settings = getOrCreateSettingsEntity(schoolId);
        settings.setYearlyDiscountPercent(yearlyDiscountPercent);
        FeeSettings saved = feeSettingsRepository.save(settings);
        return new FeeSettingsDTO(saved.getSchoolId(), saved.getYearlyDiscountPercent());
    }

    private FeeSettings getOrCreateSettingsEntity(UUID schoolId) {
        return feeSettingsRepository.findBySchoolId(schoolId).orElseGet(() -> {
            FeeSettings settings = new FeeSettings();
            settings.setId(UUID.randomUUID());
            settings.setSchoolId(schoolId);
            settings.setYearlyDiscountPercent(new BigDecimal("5.00"));
            return feeSettingsRepository.save(settings);
        });
    }

    public List<FeePaymentDTO> getRecentPayments(UUID schoolId) {
        tenantGuard.assertSchoolAccessible(schoolId);
        return paymentRepository.findBySchoolId(schoolId).stream()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .limit(10)
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // --- Calculation Helpers ---

    private BigDecimal calculateTotalDueTillDate(StudentFee sf) {
        if (RECURRENCE_ONE_TIME.equals(sf.getFeeStructure().getRecurrenceType())) {
            return sf.getAmountDue();
        }

        return sf.getAmountDue().multiply(new BigDecimal(calculateDueThroughMonth(sf)));
    }

    private int calculateDueThroughMonth(StudentFee sf) {
        if (!RECURRENCE_MONTHLY.equals(sf.getFeeStructure().getRecurrenceType())) {
            return 1;
        }

        AcademicYear year = academicYearRepository.findById(sf.getFeeStructure().getAcademicYearId())
                .orElseThrow(() -> new RuntimeException("Academic Year not found"));
        
        LocalDate sessionStart = year.getStartDate();
        LocalDate today = LocalDate.now();
        
        if (today.isBefore(sessionStart)) {
            return 0;
        }

        LocalDate effectiveDate = today.isAfter(year.getEndDate()) ? year.getEndDate() : today;
        
        // Months elapsed (inclusive of current month)
        Period period = Period.between(sessionStart.withDayOfMonth(1), effectiveDate.withDayOfMonth(1));
        int monthsElapsed = period.getYears() * 12 + period.getMonths() + 1;
        
        return Math.min(monthsElapsed, calculateTotalMonthsInSession(sf));
    }

    private int calculatePaidThroughMonth(StudentFee sf) {
        if (!RECURRENCE_MONTHLY.equals(sf.getFeeStructure().getRecurrenceType()) || sf.getAmountDue().compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        BigDecimal coveredAmount = safeAmount(sf.getAmountPaid()).add(safeAmount(sf.getDiscountAmount()));
        int coveredMonths = coveredAmount.divide(sf.getAmountDue(), 0, RoundingMode.FLOOR).intValue();
        return Math.min(coveredMonths, calculateTotalMonthsInSession(sf));
    }

    private int calculateTotalMonthsInSession(StudentFee sf) {
        if (!RECURRENCE_MONTHLY.equals(sf.getFeeStructure().getRecurrenceType())) {
            return 1;
        }

        AcademicYear year = academicYearRepository.findById(sf.getFeeStructure().getAcademicYearId())
                .orElseThrow(() -> new RuntimeException("Academic Year not found"));
        long months = ChronoUnit.MONTHS.between(year.getStartDate().withDayOfMonth(1), year.getEndDate().withDayOfMonth(1)) + 1;
        return Math.max(1, (int) months);
    }

    private BigDecimal calculateOutstandingBalance(StudentFee sf) {
        return clampZero(calculateTotalDueTillDate(sf)
                .add(calculateFine(sf))
                .subtract(safeAmount(sf.getAmountPaid()))
                .subtract(safeAmount(sf.getDiscountAmount())));
    }

    /**
     * Late-payment fine for a one-time fee whose due date (plus grace period) has
     * passed. Always recomputed from the structure's fine config rather than trusted
     * from {@link StudentFee#getFineAccrued()}, so it's correct even between
     * scheduled refreshes. Monthly-recurring fees have no single due date in the
     * current data model ({@code dueDate} is null for them) and are not fined.
     */
    private BigDecimal calculateFine(StudentFee sf) {
        FeeStructure structure = sf.getFeeStructure();
        if (structure == null || structure.getFineType() == null || structure.getFineType() == FineType.NONE) {
            return BigDecimal.ZERO;
        }

        LocalDate dueDate = sf.getDueDate();
        if (dueDate == null) {
            return BigDecimal.ZERO;
        }

        int graceDays = structure.getGracePeriodDays() == null ? 0 : Math.max(0, structure.getGracePeriodDays());
        LocalDate effectiveDueDate = dueDate.plusDays(graceDays);
        LocalDate today = LocalDate.now();
        if (!today.isAfter(effectiveDueDate)) {
            return BigDecimal.ZERO;
        }

        BigDecimal principalOutstanding = clampZero(sf.getAmountDue()
                .subtract(safeAmount(sf.getAmountPaid()))
                .subtract(safeAmount(sf.getDiscountAmount())));
        if (principalOutstanding.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal fineRate = safeAmount(structure.getFineAmount());
        if (structure.getFineType() == FineType.FLAT) {
            return fineRate;
        }

        // PERCENTAGE_PER_MONTH: fineRate% of the outstanding principal, per whole month overdue.
        long daysOverdue = ChronoUnit.DAYS.between(effectiveDueDate, today);
        int monthsOverdue = Math.max(1, (int) Math.ceil(daysOverdue / 30.0));
        BigDecimal monthlyRate = fineRate.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP);
        return principalOutstanding.multiply(monthlyRate).multiply(BigDecimal.valueOf(monthsOverdue))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void updateStatus(StudentFee studentFee) {
        // Persist the current fine for visibility/reporting even outside a live
        // recompute; calculateOutstandingBalance below recomputes it fresh regardless.
        studentFee.setFineAccrued(calculateFine(studentFee));

        BigDecimal totalDue = calculateTotalDueTillDate(studentFee);
        BigDecimal outstanding = calculateOutstandingBalance(studentFee);

        if (totalDue.compareTo(BigDecimal.ZERO) > 0 && outstanding.compareTo(BigDecimal.ZERO) == 0) {
            studentFee.setStatus(FeeStatus.PAID);
        } else if (totalDue.compareTo(BigDecimal.ZERO) > 0 && outstanding.compareTo(BigDecimal.ZERO) > 0) {
            studentFee.setStatus(FeeStatus.DUE);
        } else if (safeAmount(studentFee.getAmountPaid()).compareTo(BigDecimal.ZERO) > 0 || safeAmount(studentFee.getDiscountAmount()).compareTo(BigDecimal.ZERO) > 0) {
            studentFee.setStatus(FeeStatus.PARTIAL);
        } else {
            studentFee.setStatus(FeeStatus.UNPAID);
        }
    }

    private BigDecimal clampZero(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : amount;
    }

    private BigDecimal safeAmount(BigDecimal amount) {
        return amount == null ? BigDecimal.ZERO : amount;
    }

    private void validateMonthlyPaymentRange(StudentFee studentFee, FeePaymentDTO dto) {
        if (!RECURRENCE_MONTHLY.equals(studentFee.getFeeStructure().getRecurrenceType())) {
            return;
        }

        Integer monthFrom = dto.getMonthFrom();
        Integer monthTo = dto.getMonthTo();
        if (monthFrom == null && monthTo == null) {
            return;
        }
        if (monthFrom == null || monthTo == null || monthFrom < 1 || monthTo < monthFrom || monthTo > calculateTotalMonthsInSession(studentFee)) {
            throw new BusinessLogicException("Invalid payment month range");
        }
    }

    private String resolveRecurrenceType(String requestedRecurrenceType, String categoryName) {
        String normalizedCategory = categoryName == null ? "" : categoryName.trim().toLowerCase();
        if (normalizedCategory.contains("tuition")) {
            return RECURRENCE_MONTHLY;
        }
        if (normalizedCategory.contains("admission")) {
            return RECURRENCE_ONE_TIME;
        }

        if (RECURRENCE_MONTHLY.equalsIgnoreCase(requestedRecurrenceType)) {
            return RECURRENCE_MONTHLY;
        }
        return RECURRENCE_ONE_TIME;
    }

    private FineType resolveFineType(String requested) {
        if (requested == null || requested.isBlank()) {
            return FineType.NONE;
        }
        try {
            return FineType.valueOf(requested.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessLogicException("Invalid fine type: " + requested);
        }
    }

    private void ensureDefaultCategories(UUID schoolId) {
        ensureCategory(schoolId, "Admission fee", "One-time admission fee");
        ensureCategory(schoolId, "Tuition fee", "Monthly tuition fee");
    }

    private void ensureCategory(UUID schoolId, String name, String description) {
        if (categoryRepository.findBySchoolIdAndNameIgnoreCase(schoolId, name).isPresent()) {
            return;
        }

        FeeCategory category = new FeeCategory();
        category.setId(UUID.randomUUID());
        category.setSchoolId(schoolId);
        category.setName(name);
        category.setDescription(description);
        category.setActive(true);
        categoryRepository.save(category);
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
        dto.setFineType(entity.getFineType() != null ? entity.getFineType().name() : FineType.NONE.name());
        dto.setFineAmount(entity.getFineAmount());
        dto.setGracePeriodDays(entity.getGracePeriodDays());
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
        dto.setFineAccrued(calculateFine(entity));
        dto.setRecurrenceType(entity.getFeeStructure().getRecurrenceType());
        dto.setTotalDueTillDate(calculateTotalDueTillDate(entity));
        dto.setOutstandingBalance(calculateOutstandingBalance(entity));
        dto.setDueThroughMonth(calculateDueThroughMonth(entity));
        dto.setPaidThroughMonth(calculatePaidThroughMonth(entity));
        dto.setTotalMonthsInSession(calculateTotalMonthsInSession(entity));
        return dto;
    }

    private FeePaymentDTO mapToDTO(FeePayment entity) {
        FeePaymentDTO dto = new FeePaymentDTO();
        dto.setId(entity.getId());
        dto.setSchoolId(entity.getSchoolId());
        dto.setStudentFeeId(entity.getStudentFee().getId());
        dto.setStudentName(entity.getStudentFee().getStudent().getFirstName() + " " + entity.getStudentFee().getStudent().getLastName());
        dto.setAdmissionNumber(entity.getStudentFee().getStudent().getAdmissionNumber());
        dto.setFeeCategoryName(entity.getStudentFee().getFeeStructure().getCategory().getName());
        SchoolBranding branding = findSchoolBranding(entity.getSchoolId());
        dto.setSchoolName(branding.schoolName());
        dto.setSchoolLogoUrl(branding.logoUrl());
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

    private SchoolBranding findSchoolBranding(UUID schoolId) {
        try {
            return jdbcTemplate.queryForObject("""
                    SELECT school_name, logo_url
                    FROM tenant_school_snapshot
                    WHERE school_id = ?
                    """,
                    (rs, rowNum) -> new SchoolBranding(rs.getString("school_name"), rs.getString("logo_url")),
                    schoolId);
        } catch (DataAccessException ex) {
            return new SchoolBranding("School", null);
        }
    }

    private record SchoolBranding(String schoolName, String logoUrl) {
    }
}
