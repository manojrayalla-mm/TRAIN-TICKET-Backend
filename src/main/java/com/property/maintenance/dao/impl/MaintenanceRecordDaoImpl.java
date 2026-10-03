package com.property.maintenance.dao.impl;

import com.property.maintenance.dao.MaintenanceRecordDao;
import com.property.maintenance.model.MaintenanceRecord;
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
public class MaintenanceRecordDaoImpl implements MaintenanceRecordDao {

    private final JdbcTemplate jdbcTemplate;

    public MaintenanceRecordDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<MaintenanceRecord> recordRowMapper = (rs, rowNum) -> {
        MaintenanceRecord rec = new MaintenanceRecord();
        rec.setId(rs.getLong("id"));
        rec.setRequestId(rs.getLong("request_id"));
        rec.setPropertyId(rs.getLong("property_id"));
        rec.setTenantId(rs.getLong("tenant_id"));
        rec.setTechnicianId(rs.getObject("technician_id", Long.class));
        rec.setProblemDescription(rs.getString("problem_description"));
        rec.setWorkPerformed(rs.getString("work_performed"));

        Timestamp maintDate = rs.getTimestamp("maintenance_date");
        if (maintDate != null) rec.setMaintenanceDate(maintDate.toLocalDateTime());

        Timestamp compDate = rs.getTimestamp("completion_date");
        if (compDate != null) rec.setCompletionDate(compDate.toLocalDateTime());

        rec.setLaborCost(rs.getBigDecimal("labor_cost"));
        rec.setPartsCost(rs.getBigDecimal("parts_cost"));
        rec.setTotalCost(rs.getBigDecimal("total_cost"));
        rec.setStatus(rs.getString("status"));
        rec.setRemarks(rs.getString("remarks"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) rec.setCreatedAt(createdAt.toLocalDateTime());

        try {
            rec.setTicketNumber(rs.getString("ticket_number"));
            rec.setPropertyCode(rs.getString("property_code"));
            rec.setPropertyTitle(rs.getString("property_title"));
            rec.setTenantName(rs.getString("tenant_name"));
            rec.setTechnicianName(rs.getString("technician_name"));
        } catch (Exception ignored) {
        }

        return rec;
    };

    private static final String BASE_QUERY =
            "SELECT rec.*, " +
            "r.ticket_number, p.property_code, p.title as property_title, " +
            "CONCAT(t.first_name, ' ', t.last_name) as tenant_name, " +
            "CONCAT(tech.first_name, ' ', tech.last_name) as technician_name " +
            "FROM maintenance_records rec " +
            "JOIN maintenance_requests r ON rec.request_id = r.id " +
            "JOIN properties p ON rec.property_id = p.id " +
            "JOIN tenants t ON rec.tenant_id = t.id " +
            "LEFT JOIN technicians tech ON rec.technician_id = tech.id ";

    @Override
    public MaintenanceRecord save(MaintenanceRecord rec) {
        if (rec.getId() == null) {
            String sql = "INSERT INTO maintenance_records (request_id, property_id, tenant_id, technician_id, problem_description, work_performed, maintenance_date, completion_date, labor_cost, parts_cost, total_cost, status, remarks, created_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            LocalDateTime now = LocalDateTime.now();

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, rec.getRequestId());
                ps.setLong(2, rec.getPropertyId());
                ps.setLong(3, rec.getTenantId());
                ps.setObject(4, rec.getTechnicianId());
                ps.setString(5, rec.getProblemDescription());
                ps.setString(6, rec.getWorkPerformed());
                ps.setTimestamp(7, Timestamp.valueOf(rec.getMaintenanceDate() != null ? rec.getMaintenanceDate() : now));
                ps.setTimestamp(8, Timestamp.valueOf(rec.getCompletionDate() != null ? rec.getCompletionDate() : now));
                ps.setBigDecimal(9, rec.getLaborCost());
                ps.setBigDecimal(10, rec.getPartsCost());
                ps.setBigDecimal(11, rec.getTotalCost());
                ps.setString(12, rec.getStatus() != null ? rec.getStatus() : "COMPLETED");
                ps.setString(13, rec.getRemarks());
                ps.setTimestamp(14, Timestamp.valueOf(now));
                return ps;
            }, keyHolder);

            Number key = com.property.maintenance.dao.DaoUtils.extractGeneratedId(keyHolder);
            if (key != null) {
                rec.setId(key.longValue());
            }
            rec.setCreatedAt(now);
            return rec;
        } else {
            String sql = "UPDATE maintenance_records SET property_id = ?, tenant_id = ?, technician_id = ?, problem_description = ?, work_performed = ?, maintenance_date = ?, completion_date = ?, labor_cost = ?, parts_cost = ?, total_cost = ?, status = ?, remarks = ? WHERE id = ?";
            jdbcTemplate.update(sql, rec.getPropertyId(), rec.getTenantId(), rec.getTechnicianId(),
                    rec.getProblemDescription(), rec.getWorkPerformed(),
                    Timestamp.valueOf(rec.getMaintenanceDate()), Timestamp.valueOf(rec.getCompletionDate()),
                    rec.getLaborCost(), rec.getPartsCost(), rec.getTotalCost(),
                    rec.getStatus(), rec.getRemarks(), rec.getId());
            return rec;
        }
    }

    @Override
    public Optional<MaintenanceRecord> findById(Long id) {
        String sql = BASE_QUERY + "WHERE rec.id = ?";
        try {
            MaintenanceRecord rec = jdbcTemplate.queryForObject(sql, recordRowMapper, id);
            return Optional.ofNullable(rec);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<MaintenanceRecord> findByRequestId(Long requestId) {
        String sql = BASE_QUERY + "WHERE rec.request_id = ?";
        try {
            MaintenanceRecord rec = jdbcTemplate.queryForObject(sql, recordRowMapper, requestId);
            return Optional.ofNullable(rec);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<MaintenanceRecord> findHistory(Long propertyId, Long tenantId, Long technicianId, LocalDateTime startDate, LocalDateTime endDate) {
        StringBuilder sql = new StringBuilder(BASE_QUERY).append("WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (propertyId != null) {
            sql.append("AND rec.property_id = ? ");
            params.add(propertyId);
        }
        if (tenantId != null) {
            sql.append("AND rec.tenant_id = ? ");
            params.add(tenantId);
        }
        if (technicianId != null) {
            sql.append("AND rec.technician_id = ? ");
            params.add(technicianId);
        }
        if (startDate != null) {
            sql.append("AND rec.maintenance_date >= ? ");
            params.add(Timestamp.valueOf(startDate));
        }
        if (endDate != null) {
            sql.append("AND rec.maintenance_date <= ? ");
            params.add(Timestamp.valueOf(endDate));
        }
        sql.append("ORDER BY rec.maintenance_date DESC");

        return jdbcTemplate.query(sql.toString(), recordRowMapper, params.toArray());
    }

    @Override
    public List<MaintenanceRecord> findByPropertyId(Long propertyId) {
        String sql = BASE_QUERY + "WHERE rec.property_id = ? ORDER BY rec.maintenance_date DESC";
        return jdbcTemplate.query(sql, recordRowMapper, propertyId);
    }

    @Override
    public List<MaintenanceRecord> findByTenantId(Long tenantId) {
        String sql = BASE_QUERY + "WHERE rec.tenant_id = ? ORDER BY rec.maintenance_date DESC";
        return jdbcTemplate.query(sql, recordRowMapper, tenantId);
    }

    @Override
    public List<MaintenanceRecord> findByTechnicianId(Long technicianId) {
        String sql = BASE_QUERY + "WHERE rec.technician_id = ? ORDER BY rec.maintenance_date DESC";
        return jdbcTemplate.query(sql, recordRowMapper, technicianId);
    }
}
