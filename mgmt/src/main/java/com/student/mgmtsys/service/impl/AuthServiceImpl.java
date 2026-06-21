package com.student.mgmtsys.service.impl;

import com.student.mgmtsys.dto.LoginRequest;
import com.student.mgmtsys.dto.LoginResponse;
import com.student.mgmtsys.entity.Role;
import com.student.mgmtsys.exception.InvalidCredentialsException;
import com.student.mgmtsys.service.AuthService;
import com.student.mgmtsys.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final String INVALID_CREDENTIALS = "Invalid username or password";

    private final JwtService jwtService;
    private final String adminUsername;
    private final String adminPassword;

    public AuthServiceImpl(
            JwtService jwtService,
            @Value("${admin.username:admin}") String adminUsername,
            @Value("${admin.password:admin}") String adminPassword) {
        this.jwtService = jwtService;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public LoginResponse adminLogin(LoginRequest request) {
        if (request.getUsername() == null
                || request.getPassword() == null
                || !adminUsername.equals(request.getUsername())
                || !adminPassword.equals(request.getPassword())) {
            throw new InvalidCredentialsException(INVALID_CREDENTIALS);
        }
        String token = jwtService.generateToken(adminUsername, Role.ADMIN);
        return new LoginResponse(token, "Bearer", adminUsername, Role.ADMIN);
    }
}
