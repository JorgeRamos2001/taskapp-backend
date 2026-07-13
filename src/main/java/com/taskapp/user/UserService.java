package com.taskapp.user;

import com.taskapp.user.dto.request.ChangePasswordRequest;
import com.taskapp.user.dto.response.UserResponse;

public interface UserService {
    UserResponse getCurrentUser(String email);
    UserResponse changePassword(ChangePasswordRequest request, String email);
}
