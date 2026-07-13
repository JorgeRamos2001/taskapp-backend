package com.taskapp.user;

import com.taskapp.user.dto.request.ChangePasswordRequest;
import com.taskapp.user.dto.response.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @GetMapping("/me")
    ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        return ResponseEntity.ok(userService.getCurrentUser(authentication.getName()));
    }

    @PutMapping("/password")
    ResponseEntity<UserResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request, Authentication authentication) {
        return ResponseEntity.ok(userService.changePassword(request, authentication.getName()));
    }
}
