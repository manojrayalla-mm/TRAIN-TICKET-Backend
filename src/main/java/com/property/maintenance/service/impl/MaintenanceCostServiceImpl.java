package com.property.maintenance.service.impl;

import com.property.maintenance.dao.MaintenanceRequestDao;
import com.property.maintenance.dao.PaymentDao;
import com.property.maintenance.dto.MaintenanceCostDto;
import com.property.maintenance.dto.PaymentProcessDto;
import com.property.maintenance.exception.BadRequestException;
import com.property.maintenance.exception.ResourceNotFoundException;
import com.property.maintenance.model.Payment;
import com.property.maintenance.service.MaintenanceCostService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class MaintenanceCostServiceImpl implements MaintenanceCostService {

    private final PaymentDao paymentDao;
    private final MaintenanceRequestDao requestDao;

    public MaintenanceCostServiceImpl(PaymentDao paymentDao, MaintenanceRequestDao requestDao) {
        this.paymentDao = paymentDao;
        this.requestDao = requestDao;
    }

    @Override
    public Payment setOrUpdateCost(MaintenanceCostDto dto) {
        requestDao.findById(dto.getRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("MaintenanceRequest", "id", dto.getRequestId()));

        Payment payment = paymentDao.findByRequestId(dto.getRequestId())
                .orElseGet(() -> {
                    Payment p = new Payment();
                    p.setRequestId(dto.getRequestId());
                    p.setInvoiceNumber("INV-" + System.currentTimeMillis() + "-" + (new Random().nextInt(900) + 100));
                    p.setAmountPaid(BigDecimal.ZERO);
                    p.setPaymentStatus("PENDING");
                    p.setPaymentMethod("NOT_SPECIFIED");
                    return p;
                });

        payment.setLaborCost(dto.getLaborCost());
        payment.setPartsCost(dto.getPartsCost());
        BigDecimal total = dto.getLaborCost().add(dto.getPartsCost());
        payment.setTotalCost(total);

        if (payment.getAmountPaid().compareTo(total) >= 0 && total.compareTo(BigDecimal.ZERO) > 0) {
            payment.setPaymentStatus("PAID");
        } else if (payment.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            payment.setPaymentStatus("PARTIAL");
        } else {
            payment.setPaymentStatus("PENDING");
        }

        return paymentDao.save(payment);
    }

    @Override
    public Payment getPaymentByRequestId(Long requestId) {
        return paymentDao.findByRequestId(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment for Request", "requestId", requestId));
    }

    @Override
    public Payment getPaymentById(Long id) {
        return paymentDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }

    @Override
    public Payment getPaymentByInvoiceNumber(String invoiceNumber) {
        return paymentDao.findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "invoiceNumber", invoiceNumber));
    }

    @Override
    public List<Payment> getAllPayments(String paymentStatus) {
        return paymentDao.findAll(paymentStatus);
    }

    @Override
    public Payment processPayment(Long paymentId, PaymentProcessDto dto) {
        Payment payment = getPaymentById(paymentId);

        if ("PAID".equalsIgnoreCase(payment.getPaymentStatus())) {
            throw new BadRequestException("Invoice is already fully paid");
        }

        BigDecimal newAmountPaid = payment.getAmountPaid().add(dto.getAmount());
        String status = newAmountPaid.compareTo(payment.getTotalCost()) >= 0 ? "PAID" : "PARTIAL";

        paymentDao.recordPayment(paymentId, newAmountPaid, dto.getPaymentMethod().toUpperCase(),
                dto.getTransactionReference(), status, LocalDateTime.now());

        payment.setAmountPaid(newAmountPaid);
        payment.setPaymentMethod(dto.getPaymentMethod().toUpperCase());
        payment.setTransactionReference(dto.getTransactionReference());
        payment.setPaymentStatus(status);
        payment.setPaymentDate(LocalDateTime.now());

        return payment;
    }
}
