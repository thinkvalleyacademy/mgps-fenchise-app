package com.mgps.fee.controller;

import com.mgps.fee.dto.FeeCategoryDTO;
import com.mgps.fee.dto.FeePaymentDTO;
import com.mgps.fee.dto.FeeStructureDTO;
import com.mgps.fee.dto.StudentFeeDTO;
import com.mgps.fee.service.FeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    // --- Payments ---

    @PostMapping("/payments")
    public ResponseEntity<FeePaymentDTO> processPayment(@RequestBody FeePaymentDTO dto) {
        return ResponseEntity.ok(feeService.processPayment(dto));
    }
}
