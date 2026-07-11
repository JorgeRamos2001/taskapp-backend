package com.taskapp.auth;

import com.taskapp.auth.dto.request.LoginRequest;
import com.taskapp.auth.dto.request.LogoutRequest;
import com.taskapp.auth.dto.request.RefreshTokenRequest;
import com.taskapp.auth.dto.request.RegisterRequest;
import com.taskapp.auth.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(RefreshTokenRequest request, String email);
    void logout(LogoutRequest request, String email);
}
