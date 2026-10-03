package com.property.maintenance.service;

import com.property.maintenance.dto.LoginDto;
import com.property.maintenance.dto.RegisterDto;
import com.property.maintenance.model.User;

public interface AuthService {
    User login(LoginDto loginDto);
    User register(RegisterDto registerDto);
    User getUserById(Long id);
}
