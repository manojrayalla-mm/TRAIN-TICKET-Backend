package com.property.maintenance.controller;

import com.property.maintenance.dto.ApiResponse;
import com.property.maintenance.dto.MaintenanceCostDto;
import com.property.maintenance.dto.PaymentProcessDto;
import com.property.maintenance.model.Payment;
import com.property.maintenance.service.MaintenanceCostService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maintenance-costs")
public class MaintenanceCostController {

    private final MaintenanceCostService costService;

    public MaintenanceCostController(MaintenanceCostService costService) {
        this.costService = costService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<ApiResponse<Payment>> setCost(@Valid @RequestBody MaintenanceCostDto dto) {
        Payment payment = costService.setOrUpdateCost(dto);
        return ResponseEntity.ok(ApiResponse.success("Maintenance cost recorded successfully", payment));
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentByRequestId(@PathVariable Long requestId) {
        Payment payment = costService.getPaymentByRequestId(requestId);
        return ResponseEntity.ok(ApiResponse.success("Payment details retrieved", payment));
    }

    @GetMapping("/invoice/{invoiceNumber}")
    public ResponseEntity<ApiResponse<Payment>> getPaymentByInvoiceNumber(@PathVariable String invoiceNumber) {
        Payment payment = costService.getPaymentByInvoiceNumber(invoiceNumber);
        return ResponseEntity.ok(ApiResponse.success("Invoice details retrieved", payment));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Payment>>> getAllPayments(
            @RequestParam(required = false) String status) {
        List<Payment> payments = costService.getAllPayments(status);
        return ResponseEntity.ok(ApiResponse.success("Payment records fetched", payments));
    }

    @PostMapping("/{paymentId}/pay")
    public ResponseEntity<ApiResponse<Payment>> processPayment(
            @PathVariable Long paymentId,
            @Valid @RequestBody PaymentProcessDto dto) {
        Payment payment = costService.processPayment(paymentId, dto);
        return ResponseEntity.ok(ApiResponse.success("Payment processed successfully", payment));
    }
}
