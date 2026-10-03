package com.property.maintenance.dao;

import com.property.maintenance.model.User;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
    List<User> findAll();
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
