package com.property.maintenance.dao.impl;

import com.property.maintenance.dao.MaintenanceRequestDao;
import com.property.maintenance.model.MaintenanceRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MaintenanceRequestDaoImpl implements MaintenanceRequestDao {

    private final JdbcTemplate jdbcTemplate;

    public MaintenanceRequestDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<MaintenanceRequest> requestRowMapper = (rs, rowNum) -> {
        MaintenanceRequest r = new MaintenanceRequest();
        r.setId(rs.getLong("id"));
        r.setTicketNumber(rs.getString("ticket_number"));
        r.setPropertyId(rs.getLong("property_id"));
        r.setTenantId(rs.getLong("tenant_id"));
        r.setTechnicianId(rs.getObject("technician_id", Long.class));
        r.setCategory(rs.getString("category"));
        r.setPriority(rs.getString("priority"));
        r.setStatus(rs.getString("status"));
        r.setDescription(rs.getString("description"));

        Timestamp reqDate = rs.getTimestamp("request_date");
        if (reqDate != null) r.setRequestDate(reqDate.toLocalDateTime());

        Timestamp schedDate = rs.getTimestamp("scheduled_date");
        if (schedDate != null) r.setScheduledDate(schedDate.toLocalDateTime());

        Timestamp startDate = rs.getTimestamp("started_date");
        if (startDate != null) r.setStartedDate(startDate.toLocalDateTime());

        Timestamp compDate = rs.getTimestamp("completed_date");
        if (compDate != null) r.setCompletedDate(compDate.toLocalDateTime());

        r.setNotes(rs.getString("notes"));
        r.setCancellationReason(rs.getString("cancellation_reason"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) r.setCreatedAt(createdAt.toLocalDateTime());

        // Joined columns
        try {
            r.setPropertyCode(rs.getString("property_code"));
            r.setPropertyAddress(rs.getString("property_address"));
            r.setTenantName(rs.getString("tenant_name"));
            r.setTenantPhone(rs.getString("tenant_phone"));
            r.setTechnicianName(rs.getString("technician_name"));
            r.setTechnicianPhone(rs.getString("technician_phone"));
        } catch (Exception ignored) {
        }

        return r;
    };

    private static final String BASE_QUERY =
            "SELECT r.*, " +
            "p.property_code, p.address as property_address, " +
            "CONCAT(t.first_name, ' ', t.last_name) as tenant_name, t.phone as tenant_phone, " +
            "CONCAT(tech.first_name, ' ', tech.last_name) as technician_name, tech.phone as technician_phone " +
            "FROM maintenance_requests r " +
            "JOIN properties p ON r.property_id = p.id " +
            "JOIN tenants t ON r.tenant_id = t.id " +
            "LEFT JOIN technicians tech ON r.technician_id = tech.id ";

    @Override
    public MaintenanceRequest save(MaintenanceRequest r) {
        if (r.getId() == null) {
            String sql = "INSERT INTO maintenance_requests (ticket_number, property_id, tenant_id, technician_id, category, priority, status, description, request_date, scheduled_date, started_date, completed_date, notes, cancellation_reason, created_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            LocalDateTime now = LocalDateTime.now();

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, r.getTicketNumber());
                ps.setLong(2, r.getPropertyId());
                ps.setLong(3, r.getTenantId());
                ps.setObject(4, r.getTechnicianId());
                ps.setString(5, r.getCategory());
                ps.setString(6, r.getPriority() != null ? r.getPriority() : "MEDIUM");
                ps.setString(7, r.getStatus() != null ? r.getStatus() : "PENDING");
                ps.setString(8, r.getDescription());
                ps.setTimestamp(9, Timestamp.valueOf(r.getRequestDate() != null ? r.getRequestDate() : now));
                ps.setTimestamp(10, r.getScheduledDate() != null ? Timestamp.valueOf(r.getScheduledDate()) : null);
                ps.setTimestamp(11, r.getStartedDate() != null ? Timestamp.valueOf(r.getStartedDate()) : null);
                ps.setTimestamp(12, r.getCompletedDate() != null ? Timestamp.valueOf(r.getCompletedDate()) : null);
                ps.setString(13, r.getNotes());
                ps.setString(14, r.getCancellationReason());
                ps.setTimestamp(15, Timestamp.valueOf(now));
                return ps;
            }, keyHolder);

            Number key = com.property.maintenance.dao.DaoUtils.extractGeneratedId(keyHolder);
            if (key != null) {
                r.setId(key.longValue());
            }
            r.setCreatedAt(now);
            return r;
        } else {
            String sql = "UPDATE maintenance_requests SET property_id = ?, tenant_id = ?, technician_id = ?, category = ?, priority = ?, status = ?, description = ?, scheduled_date = ?, started_date = ?, completed_date = ?, notes = ?, cancellation_reason = ? WHERE id = ?";
            jdbcTemplate.update(sql, r.getPropertyId(), r.getTenantId(), r.getTechnicianId(),
                    r.getCategory(), r.getPriority(), r.getStatus(), r.getDescription(),
                    r.getScheduledDate() != null ? Timestamp.valueOf(r.getScheduledDate()) : null,
                    r.getStartedDate() != null ? Timestamp.valueOf(r.getStartedDate()) : null,
                    r.getCompletedDate() != null ? Timestamp.valueOf(r.getCompletedDate()) : null,
                    r.getNotes(), r.getCancellationReason(), r.getId());
            return r;
        }
    }

    @Override
    public Optional<MaintenanceRequest> findById(Long id) {
        String sql = BASE_QUERY + "WHERE r.id = ?";
        try {
            MaintenanceRequest r = jdbcTemplate.queryForObject(sql, requestRowMapper, id);
            return Optional.ofNullable(r);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<MaintenanceRequest> findByTicketNumber(String ticketNumber) {
        String sql = BASE_QUERY + "WHERE r.ticket_number = ?";
        try {
            MaintenanceRequest r = jdbcTemplate.queryForObject(sql, requestRowMapper, ticketNumber);
            return Optional.ofNullable(r);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<MaintenanceRequest> findAll(String status, String priority, Long propertyId, Long technicianId) {
        StringBuilder sql = new StringBuilder(BASE_QUERY).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isBlank()) {
            sql.append("AND r.status = ? ");
            params.add(status);
        }
        if (priority != null && !priority.isBlank()) {
            sql.append("AND r.priority = ? ");
            params.add(priority);
        }
        if (propertyId != null) {
            sql.append("AND r.property_id = ? ");
            params.add(propertyId);
        }
        if (technicianId != null) {
            sql.append("AND r.technician_id = ? ");
            params.add(technicianId);
        }
        sql.append("ORDER BY r.request_date DESC");

        return jdbcTemplate.query(sql.toString(), requestRowMapper, params.toArray());
    }

    @Override
    public List<MaintenanceRequest> findByTenantId(Long tenantId) {
        String sql = BASE_QUERY + "WHERE r.tenant_id = ? ORDER BY r.request_date DESC";
        return jdbcTemplate.query(sql, requestRowMapper, tenantId);
    }

    @Override
    public List<MaintenanceRequest> findByTechnicianId(Long technicianId) {
        String sql = BASE_QUERY + "WHERE r.technician_id = ? ORDER BY r.request_date DESC";
        return jdbcTemplate.query(sql, requestRowMapper, technicianId);
    }

    @Override
    public int updateStatus(Long id, String status, String notes) {
        String sql = "UPDATE maintenance_requests SET status = ?, notes = COALESCE(?, notes) WHERE id = ?";
        return jdbcTemplate.update(sql, status, notes, id);
    }

    @Override
    public int assignTechnician(Long id, Long technicianId, LocalDateTime scheduledDate, String notes) {
        String sql = "UPDATE maintenance_requests SET technician_id = ?, status = 'ASSIGNED', scheduled_date = ?, notes = COALESCE(?, notes) WHERE id = ?";
        return jdbcTemplate.update(sql, technicianId, scheduledDate != null ? Timestamp.valueOf(scheduledDate) : null, notes, id);
    }

    @Override
    public int markCompleted(Long id, LocalDateTime completedDate, String notes) {
        String sql = "UPDATE maintenance_requests SET status = 'COMPLETED', completed_date = ?, notes = COALESCE(?, notes) WHERE id = ?";
        return jdbcTemplate.update(sql, Timestamp.valueOf(completedDate != null ? completedDate : LocalDateTime.now()), notes, id);
    }

    @Override
    public int markCancelled(Long id, String reason) {
        String sql = "UPDATE maintenance_requests SET status = 'CANCELLED', cancellation_reason = ? WHERE id = ?";
        return jdbcTemplate.update(sql, reason, id);
    }
}
