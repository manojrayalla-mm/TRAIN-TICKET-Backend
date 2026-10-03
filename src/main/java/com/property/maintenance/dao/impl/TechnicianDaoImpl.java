package com.property.maintenance.dao.impl;

import com.property.maintenance.dao.TechnicianDao;
import com.property.maintenance.model.Technician;
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
public class TechnicianDaoImpl implements TechnicianDao {

    private final JdbcTemplate jdbcTemplate;

    public TechnicianDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Technician> technicianRowMapper = (rs, rowNum) -> {
        Technician t = new Technician();
        t.setId(rs.getLong("id"));
        t.setUserId(rs.getObject("user_id", Long.class));
        t.setFirstName(rs.getString("first_name"));
        t.setLastName(rs.getString("last_name"));
        t.setEmail(rs.getString("email"));
        t.setPhone(rs.getString("phone"));
        t.setSpecialization(rs.getString("specialization"));
        t.setIsAvailable(rs.getBoolean("is_available"));
        t.setHourlyRate(rs.getBigDecimal("hourly_rate"));
        t.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        t.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
        return t;
    };

    @Override
    public Technician save(Technician t) {
        if (t.getId() == null) {
            String sql = "INSERT INTO technicians (user_id, first_name, last_name, email, phone, specialization, is_available, hourly_rate, status, created_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            LocalDateTime now = LocalDateTime.now();

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setObject(1, t.getUserId());
                ps.setString(2, t.getFirstName());
                ps.setString(3, t.getLastName());
                ps.setString(4, t.getEmail());
                ps.setString(5, t.getPhone());
                ps.setString(6, t.getSpecialization());
                ps.setBoolean(7, t.getIsAvailable() != null ? t.getIsAvailable() : true);
                ps.setBigDecimal(8, t.getHourlyRate());
                ps.setString(9, t.getStatus() != null ? t.getStatus() : "ACTIVE");
                ps.setTimestamp(10, Timestamp.valueOf(now));
                return ps;
            }, keyHolder);

            Number key = com.property.maintenance.dao.DaoUtils.extractGeneratedId(keyHolder);
            if (key != null) {
                t.setId(key.longValue());
            }
            t.setCreatedAt(now);
            return t;
        } else {
            String sql = "UPDATE technicians SET user_id = ?, first_name = ?, last_name = ?, email = ?, phone = ?, specialization = ?, is_available = ?, hourly_rate = ?, status = ? WHERE id = ?";
            jdbcTemplate.update(sql, t.getUserId(), t.getFirstName(), t.getLastName(), t.getEmail(),
                    t.getPhone(), t.getSpecialization(), t.getIsAvailable(), t.getHourlyRate(),
                    t.getStatus(), t.getId());
            return t;
        }
    }

    @Override
    public Optional<Technician> findById(Long id) {
        String sql = "SELECT * FROM technicians WHERE id = ?";
        try {
            Technician t = jdbcTemplate.queryForObject(sql, technicianRowMapper, id);
            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Technician> findByUserId(Long userId) {
        String sql = "SELECT * FROM technicians WHERE user_id = ?";
        try {
            Technician t = jdbcTemplate.queryForObject(sql, technicianRowMapper, userId);
            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Technician> findByEmail(String email) {
        String sql = "SELECT * FROM technicians WHERE email = ?";
        try {
            Technician t = jdbcTemplate.queryForObject(sql, technicianRowMapper, email);
            return Optional.ofNullable(t);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Technician> findAll(String specialization, Boolean isAvailable, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM technicians WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (specialization != null && !specialization.isBlank()) {
            sql.append("AND specialization = ? ");
            params.add(specialization);
        }
        if (isAvailable != null) {
            sql.append("AND is_available = ? ");
            params.add(isAvailable);
        }
        if (status != null && !status.isBlank()) {
            sql.append("AND status = ? ");
            params.add(status);
        }
        sql.append("ORDER BY id ASC");

        return jdbcTemplate.query(sql.toString(), technicianRowMapper, params.toArray());
    }

    @Override
    public List<Technician> findAvailableBySpecialization(String specialization) {
        String sql = "SELECT * FROM technicians WHERE specialization = ? AND is_available = TRUE AND status = 'ACTIVE' ORDER BY id ASC";
        return jdbcTemplate.query(sql, technicianRowMapper, specialization);
    }

    @Override
    public int updateAvailability(Long id, boolean isAvailable) {
        String sql = "UPDATE technicians SET is_available = ? WHERE id = ?";
        return jdbcTemplate.update(sql, isAvailable, id);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM technicians WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM technicians WHERE email = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }
}
