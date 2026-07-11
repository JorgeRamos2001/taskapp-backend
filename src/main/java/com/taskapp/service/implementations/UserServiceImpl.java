package com.taskapp.service.implementations;

import com.taskapp.dto.request.ChangePasswordRequest;
import com.taskapp.dto.response.UserResponse;
import com.taskapp.entity.User;
import com.taskapp.exception.EntityNotFoundException;
import com.taskapp.exception.ValidationException;
import com.taskapp.repository.UserRepository;
import com.taskapp.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse changePassword(ChangePasswordRequest request, String email) {
        log.warn("Changing password for user: {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!passwordEncoder.matches(request.oldPassword(), user.getPassword())) {
            throw new ValidationException("Old password does not match.");
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        User savedUser = userRepository.save(user);
        return new UserResponse(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getUrlAvatar()
        );
    }
}
