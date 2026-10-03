package com.property.maintenance.dao.impl;

import com.property.maintenance.dao.TenantDao;
import com.property.maintenance.model.Tenant;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TenantDaoImpl implements TenantDao {

    private final JdbcTemplate jdbcTemplate;

    public TenantDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Tenant> tenantRowMapper = (rs, rowNum) -> {
        Tenant t = new Tenant();
        t.setId(rs.getLong("id"));
        t.setUserId(rs.getObject("user_id", Long.class));
        t.setFirstName(rs.getString("first_name"));
        t.setLastName(rs.getString("last_name"));
        t.setEmail(rs.getString("email"));
        t.setPhone(rs.getString("phone"));
        t.setEmergencyContact(rs.getString("emergency_contact"));
        t.setPropertyId(rs.getObject("property_id", Long.class));

        Date startDate = rs.getDate("lease_start_date");
        if (startDate != null) t.setLeaseStartDate(startDate.toLocalDate());

        Date endDate = rs.getDate("lease_end_date");
        if (endDate != null) t.setLeaseEndDate(endDate.toLocalDate());

        t.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        t.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);

        // Optional columns from joined queries
        try {
            t.setPropertyCode(rs.getString("property_code"));
            t.setPropertyTitle(rs.getString("property_title"));
        } catch (Exception ignored) {
        }

        return t;
    };

    @Override
    public Tenant save(Tenant t) {
        if (t.getId() == null) {
            String sql = "INSERT INTO tenants (user_id, first_name, last_name, email, phone, emergency_contact, property_id, lease_start_date, lease_end_date, status, created_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            LocalDateTime now = LocalDateTime.now();

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, t.getUserId());
                ps.setString(2, t.getFirstName());
                ps.setString(3, t.getLastName());
                ps.setString(4, t.getEmail());
                ps.setString(5, t.getPhone());
                ps.setString(6, t.getEmergencyContact());
                ps.setObject(7, t.getPropertyId());
                ps.setDate(8, t.getLeaseStartDate() != null ? Date.valueOf(t.getLeaseStartDate()) : null);
                ps.setDate(9, t.getLeaseEndDate() != null ? Date.valueOf(t.getLeaseEndDate()) : null);
                ps.setString(10, t.getStatus() != null ? t.getStatus() : "ACTIVE");
                ps.setTimestamp(11, Timestamp.valueOf(now));
                return ps;
            }, keyHolder);

            Number key = com.property.maintenance.dao.DaoUtils.extractGeneratedId(keyHolder);
            if (key != null) {
                t.setId(key.longValue());
            }
            t.setCreatedAt(now);
            return t;
        } else {
            String sql = "UPDATE tenants SET user_id = ?, first_name = ?, last_name = ?, email = ?, phone = ?, emergency_contact = ?, property_id = ?, lease_start_date = ?, lease_end_date = ?, status = ? WHERE id = ?";
            jdbcTemplate.update(sql, t.getUserId(), t.getFirstName(), t.getLastName(), t.getEmail(),
                    t.getPhone(), t.getEmergencyContact(), t.getPropertyId(),
                    t.getLeaseStartDate() != null ? Date.valueOf(t.getLeaseStartDate()) : null,
                    t.getLeaseEndDate() != null ? Date.valueOf(t.getLeaseEndDate()) : null,
                    t.getStatus(), t.getId());
            return t;
        }
    }

    @Override
    public Optional<Tenant> findById(Long id) {
        String sql = "SELECT t.*, p.property_code, p.title as property_title FROM tenants t " +
                     "LEFT JOIN properties p ON t.property_id = p.id WHERE t.id = ?";
        try {
            Tenant t = jdbcTemplate.queryForObject(sql, tenantRowMapper, id);
            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Tenant> findByUserId(Long userId) {
        String sql = "SELECT t.*, p.property_code, p.title as property_title FROM tenants t " +
                     "LEFT JOIN properties p ON t.property_id = p.id WHERE t.user_id = ?";
        try {
            Tenant t = jdbcTemplate.queryForObject(sql, tenantRowMapper, userId);
            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Tenant> findByEmail(String email) {
        String sql = "SELECT t.*, p.property_code, p.title as property_title FROM tenants t " +
                     "LEFT JOIN properties p ON t.property_id = p.id WHERE t.email = ?";
        try {
            Tenant t = jdbcTemplate.queryForObject(sql, tenantRowMapper, email);
            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Tenant> findAll(String status, Long propertyId) {
        StringBuilder sql = new StringBuilder("SELECT t.*, p.property_code, p.title as property_title FROM tenants t " +
                                              "LEFT JOIN properties p ON t.property_id = p.id WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isBlank()) {
            sql.append("AND t.status = ? ");
            params.add(status);
        }
        if (propertyId != null) {
            sql.append("AND t.property_id = ? ");
            params.add(propertyId);
        }
        sql.append("ORDER BY t.id ASC");

        return jdbcTemplate.query(sql.toString(), tenantRowMapper, params.toArray());
    }

    @Override
    public int assignProperty(Long tenantId, Long propertyId) {
        String sql = "UPDATE tenants SET property_id = ? WHERE id = ?";
        return jdbcTemplate.update(sql, propertyId, tenantId);
    }

    @Override
    public int unassignProperty(Long tenantId) {
        String sql = "UPDATE tenants SET property_id = NULL WHERE id = ?";
        return jdbcTemplate.update(sql, tenantId);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM tenants WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM tenants WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}
