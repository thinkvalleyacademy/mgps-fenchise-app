package com.mgps.fee.controller;

import com.mgps.fee.dto.FeeCategoryDTO;
import com.mgps.fee.dto.FeePaymentDTO;
import com.mgps.fee.dto.FeeReportDTOs.*;
import com.mgps.fee.dto.FeeStructureDTO;
import com.mgps.fee.dto.StudentFeeDTO;
import com.mgps.fee.service.FeeService;
import com.mgps.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/fees")
public class FeeController {

    @Autowired
    private FeeService feeService;

    // --- Fee Categories ---

    @PostMapping("/categories")
    public ResponseEntity<FeeCategoryDTO> createCategory(@RequestBody FeeCategoryDTO dto) {
        return ResponseEntity.ok(feeService.createCategory(dto));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<FeeCategoryDTO>> getCategories(@RequestParam UUID schoolId) {
        return ResponseEntity.ok(feeService.getCategories(schoolId));
    }

    // --- Fee Structures ---

    @PostMapping("/structures")
    public ResponseEntity<FeeStructureDTO> createStructure(@RequestBody FeeStructureDTO dto) {
        return ResponseEntity.ok(feeService.createStructure(dto));
    }

    @GetMapping("/structures")
    public ResponseEntity<List<FeeStructureDTO>> getStructures(@RequestParam UUID schoolId, @RequestParam UUID academicYearId) {
        return ResponseEntity.ok(feeService.getStructures(schoolId, academicYearId));
    }

    // --- Student Fees ---

    @PostMapping("/assign")
    public ResponseEntity<StudentFeeDTO> assignFee(@RequestParam UUID studentId, @RequestParam UUID structureId) {
        return ResponseEntity.ok(feeService.assignFeeToStudent(studentId, structureId));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentFeeDTO>> getStudentFees(@PathVariable UUID studentId) {
        return ResponseEntity.ok(feeService.getStudentFees(studentId));
    }

    @PatchMapping("/student-fees/{id}/discount")
    public ResponseEntity<ApiResponse<?>> applyDiscount(@PathVariable UUID id, 
                                                        @RequestParam BigDecimal amount, 
                                                        @RequestParam String reason) {
        feeService.applyDiscount(id, amount, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "Discount applied successfully"));
    }

    // --- Payments ---

    @PostMapping("/payments")
    public ResponseEntity<FeePaymentDTO> processPayment(@RequestBody FeePaymentDTO dto) {
        return ResponseEntity.ok(feeService.processPayment(dto));
    }

    @GetMapping("/payments/recent")
    public ResponseEntity<List<FeePaymentDTO>> getRecentPayments(@RequestParam UUID schoolId) {
        return ResponseEntity.ok(feeService.getRecentPayments(schoolId));
    }

    // --- Reports ---

    @GetMapping("/reports/school-overall")
    public ResponseEntity<SchoolFeeReport> getSchoolReport(@RequestParam UUID schoolId, @RequestParam UUID academicYearId) {
        return ResponseEntity.ok(feeService.getSchoolOverallReport(schoolId, academicYearId));
    }

    @GetMapping("/reports/class-wise")
    public ResponseEntity<List<ClassFeeReport>> getClassReport(@RequestParam UUID schoolId, @RequestParam UUID academicYearId) {
        return ResponseEntity.ok(feeService.getClassWiseReport(schoolId, academicYearId));
    }

    @GetMapping("/reports/student-wise")
    public ResponseEntity<List<StudentFeeReport>> getStudentReport(@RequestParam UUID classId) {
        return ResponseEntity.ok(feeService.getStudentWiseReport(classId));
    }
}
