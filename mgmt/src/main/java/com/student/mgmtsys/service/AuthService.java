package com.student.mgmtsys.service;

import com.student.mgmtsys.dto.LoginRequest;
import com.student.mgmtsys.dto.LoginResponse;

public interface AuthService {

    LoginResponse adminLogin(LoginRequest request);
}
