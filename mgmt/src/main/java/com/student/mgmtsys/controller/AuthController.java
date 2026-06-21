package com.student.mgmtsys.controller;

import com.student.mgmtsys.dto.LoginRequest;
import com.student.mgmtsys.dto.LoginResponse;
import com.student.mgmtsys.service.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Slf4j
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/admin/login")
    public ResponseEntity<LoginResponse> adminLogin(@RequestBody LoginRequest request) {
        log.info("Admin login attempt for username {}", request.getUsername());
        return ResponseEntity.ok(authService.adminLogin(request));
    }
}
