package com.taskapp.service;

import com.taskapp.dto.request.ChangePasswordRequest;
import com.taskapp.dto.response.UserResponse;

public interface UserService {
    UserResponse changePassword(ChangePasswordRequest request, String email);
}
