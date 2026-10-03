package com.property.maintenance.dao;

import com.property.maintenance.model.Payment;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentDao {
    Payment save(Payment payment);
    Optional<Payment> findById(Long id);
    Optional<Payment> findByRequestId(Long requestId);
    Optional<Payment> findByInvoiceNumber(String invoiceNumber);
    List<Payment> findAll(String paymentStatus);
    int recordPayment(Long id, BigDecimal amountPaid, String paymentMethod, String txnRef, String status, LocalDateTime paymentDate);
}
