package com.taskapp.service;

import com.taskapp.dto.request.LoginRequest;
import com.taskapp.dto.request.LogoutRequest;
import com.taskapp.dto.request.RefreshTokenRequest;
import com.taskapp.dto.request.RegisterRequest;
import com.taskapp.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request, String email);
    void logout(LogoutRequest request, String email);
}
