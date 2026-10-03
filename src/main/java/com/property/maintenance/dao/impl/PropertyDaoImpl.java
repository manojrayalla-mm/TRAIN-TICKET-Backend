package com.property.maintenance.dao.impl;

import com.property.maintenance.dao.PropertyDao;
import com.property.maintenance.model.Property;
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
public class PropertyDaoImpl implements PropertyDao {

    private final JdbcTemplate jdbcTemplate;

    public PropertyDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Property> propertyRowMapper = (rs, rowNum) -> {
        Property p = new Property();
        p.setId(rs.getLong("id"));
        p.setPropertyCode(rs.getString("property_code"));
        p.setTitle(rs.getString("title"));
        p.setAddress(rs.getString("address"));
        p.setCity(rs.getString("city"));
        p.setState(rs.getString("state"));
        p.setZipCode(rs.getString("zip_code"));
        p.setPropertyType(rs.getString("property_type"));
        p.setStatus(rs.getString("status"));
        p.setRentAmount(rs.getBigDecimal("rent_amount"));
        p.setBedrooms(rs.getObject("bedrooms", Integer.class));
        p.setBathrooms(rs.getObject("bathrooms", Integer.class));
        p.setDescription(rs.getString("description"));
        Timestamp ts = rs.getTimestamp("created_at");
        p.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
        return p;
    };

    @Override
    public Property save(Property p) {
        if (p.getId() == null) {
            String sql = "INSERT INTO properties (property_code, title, address, city, state, zip_code, property_type, status, rent_amount, bedrooms, bathrooms, description, created_at) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();
            LocalDateTime now = LocalDateTime.now();

            jdbcTemplate.update(con -> {
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, p.getPropertyCode());
                ps.setString(2, p.getTitle());
                ps.setString(3, p.getAddress());
                ps.setString(4, p.getCity());
                ps.setString(5, p.getState());
                ps.setString(6, p.getZipCode());
                ps.setString(7, p.getPropertyType());
                ps.setString(8, p.getStatus() != null ? p.getStatus() : "AVAILABLE");
                ps.setBigDecimal(9, p.getRentAmount());
                ps.setObject(10, p.getBedrooms());
                ps.setObject(11, p.getBathrooms());
                ps.setString(12, p.getDescription());
                ps.setTimestamp(13, Timestamp.valueOf(now));
                return ps;
            }, keyHolder);

            Number key = com.property.maintenance.dao.DaoUtils.extractGeneratedId(keyHolder);
            if (key != null) {
                p.setId(key.longValue());
            }
            p.setCreatedAt(now);
            return p;
        } else {
            String sql = "UPDATE properties SET property_code = ?, title = ?, address = ?, city = ?, state = ?, zip_code = ?, property_type = ?, status = ?, rent_amount = ?, bedrooms = ?, bathrooms = ?, description = ? WHERE id = ?";
            jdbcTemplate.update(sql, p.getPropertyCode(), p.getTitle(), p.getAddress(), p.getCity(), p.getState(),
                    p.getZipCode(), p.getPropertyType(), p.getStatus(), p.getRentAmount(), p.getBedrooms(),
                    p.getBathrooms(), p.getDescription(), p.getId());
            return p;
        }
    }

    @Override
    public Optional<Property> findById(Long id) {
        String sql = "SELECT * FROM properties WHERE id = ?";
        try {
            Property p = jdbcTemplate.queryForObject(sql, propertyRowMapper, id);
            return Optional.ofNullable(p);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Property> findByPropertyCode(String code) {
        String sql = "SELECT * FROM properties WHERE property_code = ?";
        try {
            Property p = jdbcTemplate.queryForObject(sql, propertyRowMapper, code);
            return Optional.ofNullable(p);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Property> findAll(String status, String propertyType, String city) {
        StringBuilder sql = new StringBuilder("SELECT * FROM properties WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (status != null && !status.isBlank()) {
            sql.append("AND status = ? ");
            params.add(status);
        }
        if (propertyType != null && !propertyType.isBlank()) {
            sql.append("AND property_type = ? ");
            params.add(propertyType);
        }
        if (city != null && !city.isBlank()) {
            sql.append("AND LOWER(city) = LOWER(?) ");
            params.add(city);
        }
        sql.append("ORDER BY id ASC");

        return jdbcTemplate.query(sql.toString(), propertyRowMapper, params.toArray());
    }

    @Override
    public int updateStatus(Long id, String status) {
        String sql = "UPDATE properties SET status = ? WHERE id = ?";
        return jdbcTemplate.update(sql, status, id);
    }

    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM properties WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public boolean existsByPropertyCode(String code) {
        String sql = "SELECT COUNT(*) FROM properties WHERE property_code = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code);
        return count != null && count > 0;
    }
}
