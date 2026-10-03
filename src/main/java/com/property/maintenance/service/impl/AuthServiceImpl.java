package com.property.maintenance.service.impl;

import com.property.maintenance.dao.UserDao;
import com.property.maintenance.dto.LoginDto;
import com.property.maintenance.dto.RegisterDto;
import com.property.maintenance.exception.BadRequestException;
import com.property.maintenance.exception.ResourceNotFoundException;
import com.property.maintenance.model.User;
import com.property.maintenance.service.AuthService;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;

    public AuthServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User login(LoginDto loginDto) {
        User user = userDao.findByUsernameOrEmail(loginDto.getUsernameOrEmail())
                .orElseThrow(() -> new BadRequestException("Invalid username or email"));

        if (!user.getPassword().equals(loginDto.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        return user;
    }

    @Override
    public User register(RegisterDto registerDto) {
        if (userDao.existsByUsername(registerDto.getUsername())) {
            throw new BadRequestException("Username is already taken");
        }

        if (userDao.existsByEmail(registerDto.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setRole(registerDto.getRole().toUpperCase());

        return userDao.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
