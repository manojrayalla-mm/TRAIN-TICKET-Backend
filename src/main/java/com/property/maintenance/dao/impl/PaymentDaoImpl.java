package com.property.maintenance.dao.impl;

import com.property.maintenance.dao.PaymentDao;
import com.property.maintenance.model.Payment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class PaymentDaoImpl implements PaymentDao {

    private final JdbcTemplate jdbcTemplate;

    public PaymentDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Payment> paymentRowMapper = (rs, rowNum) -> {
        Payment p = new Payment();
        p.setId(rs.getLong("id"));
        p.setRequestId(rs.getLong("request_id"));
        p.setInvoiceNumber(rs.getString("invoice_number"));
        p.setLaborCost(rs.getBigDecimal("labor_cost"));
        p.setPartsCost(rs.getBigDecimal("parts_cost"));
        p.setTotalCost(rs.getBigDecimal("total_cost"));
        p.setAmountPaid(rs.getBigDecimal("amount_paid"));
        p.setPaymentStatus(rs.getString("payment_status"));
        p.setPaymentMethod(rs.getString("payment_method"));

        Timestamp payDate = rs.getTimestamp("payment_date");
        if (payDate != null) p.setPaymentDate(payDate.toLocalDateTime());

        p.setTransactionReference(rs.getString("transaction_reference"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) p.setCreatedAt(createdAt.toLocalDateTime());

        try {
            p.setTicketNumber(rs.getString("ticket_number"));
            p.setPropertyCode(rs.getString("property_code"));
        } catch (Exception ignored) {
        }

        return p;
    };

    private static final String BASE_QUERY =
            "SELECT pay.*, r.ticket_number, prop.property_code " +
            "FROM payments pay " +
            "JOIN maintenance_requests r ON pay.request_id = r.id " +
            "JOIN properties prop ON r.property_id = prop.id ";

    @Override
    public Payment save(Payment p) {
        if (p.getId() == null) {
            String sql = "INSERT INTO payments (request_id, invoice_number, labor_cost, parts_cost, total_cost, amount_paid, payment_status, payment_method, payment_date, transaction_reference, created_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            LocalDateTime now = LocalDateTime.now();

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, p.getRequestId());
                ps.setString(2, p.getInvoiceNumber());
                ps.setBigDecimal(3, p.getLaborCost());
                ps.setBigDecimal(4, p.getPartsCost());
                ps.setBigDecimal(5, p.getTotalCost());
                ps.setBigDecimal(6, p.getAmountPaid());
                ps.setString(7, p.getPaymentStatus() != null ? p.getPaymentStatus() : "PENDING");
                ps.setString(8, p.getPaymentMethod() != null ? p.getPaymentMethod() : "NOT_SPECIFIED");
                ps.setTimestamp(9, p.getPaymentDate() != null ? Timestamp.valueOf(p.getPaymentDate()) : null);
                ps.setString(10, p.getTransactionReference());
                ps.setTimestamp(11, Timestamp.valueOf(now));
                return ps;
            }, keyHolder);

            Number key = com.property.maintenance.dao.DaoUtils.extractGeneratedId(keyHolder);
            if (key != null) {
                p.setId(key.longValue());
            }
            p.setCreatedAt(now);
            return p;
        } else {
            String sql = "UPDATE payments SET labor_cost = ?, parts_cost = ?, total_cost = ?, amount_paid = ?, payment_status = ?, payment_method = ?, payment_date = ?, transaction_reference = ? WHERE id = ?";
            jdbcTemplate.update(sql, p.getLaborCost(), p.getPartsCost(), p.getTotalCost(),
                    p.getAmountPaid(), p.getPaymentStatus(), p.getPaymentMethod(),
                    p.getPaymentDate() != null ? Timestamp.valueOf(p.getPaymentDate()) : null,
                    p.getTransactionReference(), p.getId());
            return p;
        }
    }

    @Override
    public Optional<Payment> findById(Long id) {
        String sql = BASE_QUERY + "WHERE pay.id = ?";
        try {
            Payment p = jdbcTemplate.queryForObject(sql, paymentRowMapper, id);
            return Optional.ofNullable(p);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Payment> findByRequestId(Long requestId) {
        String sql = BASE_QUERY + "WHERE pay.request_id = ?";
        try {
            Payment p = jdbcTemplate.queryForObject(sql, paymentRowMapper, requestId);
            return Optional.ofNullable(p);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Payment> findByInvoiceNumber(String invoiceNumber) {
        String sql = BASE_QUERY + "WHERE pay.invoice_number = ?";
        try {
            Payment p = jdbcTemplate.queryForObject(sql, paymentRowMapper, invoiceNumber);
            return Optional.ofNullable(p);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Payment> findAll(String paymentStatus) {
        StringBuilder sql = new StringBuilder(BASE_QUERY).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (paymentStatus != null && !paymentStatus.isBlank()) {
            sql.append("AND pay.payment_status = ? ");
            params.add(paymentStatus);
        }
        sql.append("ORDER BY pay.id DESC");

        return jdbcTemplate.query(sql.toString(), paymentRowMapper, params.toArray());
    }

    @Override
    public int recordPayment(Long id, BigDecimal amountPaid, String paymentMethod, String txnRef, String status, LocalDateTime paymentDate) {
        String sql = "UPDATE payments SET amount_paid = ?, payment_method = ?, transaction_reference = ?, payment_status = ?, payment_date = ? WHERE id = ?";
        return jdbcTemplate.update(sql, amountPaid, paymentMethod, txnRef, status, Timestamp.valueOf(paymentDate), id);
    }
}
