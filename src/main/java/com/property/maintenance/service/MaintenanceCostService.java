package com.property.maintenance.service;

import com.property.maintenance.dto.MaintenanceCostDto;
import com.property.maintenance.dto.PaymentProcessDto;
import com.property.maintenance.model.Payment;
import java.util.List;

public interface MaintenanceCostService {
    Payment setOrUpdateCost(MaintenanceCostDto dto);
    Payment getPaymentByRequestId(Long requestId);
    Payment getPaymentById(Long id);
    Payment getPaymentByInvoiceNumber(String invoiceNumber);
    List<Payment> getAllPayments(String paymentStatus);
    Payment processPayment(Long paymentId, PaymentProcessDto dto);
}
