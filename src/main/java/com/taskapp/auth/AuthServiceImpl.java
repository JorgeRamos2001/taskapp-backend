package com.taskapp.auth;

import com.taskapp.auth.dto.request.LoginRequest;
import com.taskapp.auth.dto.request.LogoutRequest;
import com.taskapp.auth.dto.request.RefreshTokenRequest;
import com.taskapp.auth.dto.request.RegisterRequest;
import com.taskapp.auth.dto.response.AuthResponse;
import com.taskapp.auth.entity.RefreshToken;
import com.taskapp.auth.entity.User;
import com.taskapp.auth.entity.enums.UserProvider;
import com.taskapp.auth.repository.RefreshTokenRepository;
import com.taskapp.auth.repository.UserRepository;
import com.taskapp.auth.security.JwtService;
import com.taskapp.shared.exception.DuplicateException;
import com.taskapp.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Value( "${app.jwt.refresh-expiration-ms}")
    private Long REFRESH_TOKEN_EXPIRATION_TIME;

    @Value( "${app.jwt.access-expiration-ms}")
    private Long ACCESS_TOKEN_EXPIRATION_TIME;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering user: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            throw new DuplicateException("Email already exists");
        }

        User user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .provider(UserProvider.LOCAL)
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = generateAndSaveRefreshToken(savedUser);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                ACCESS_TOKEN_EXPIRATION_TIME
        );
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Logging in user: {}", request.email());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = generateAndSaveRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                ACCESS_TOKEN_EXPIRATION_TIME
        );
    }

    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request, String email) {
        log.warn("Refreshing token for user: {}", email);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken()).orElseThrow(() -> new EntityNotFoundException("Refresh token not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!user.getId().equals(refreshToken.getUser().getId())) {
            throw new BadCredentialsException("Invalid refresh token.");
        }

        String accessToken = jwtService.generateAccessToken(user);

        return new AuthResponse(
                accessToken,
                request.refreshToken(),
                "Bearer",
                ACCESS_TOKEN_EXPIRATION_TIME
        );
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request, String email) {
        log.info("Logging out user: {}", email);

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken()).orElseThrow(() -> new EntityNotFoundException("Refresh token not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!user.getId().equals(refreshToken.getUser().getId())) {
            throw new BadCredentialsException("Invalid refresh token.");
        }

        refreshTokenRepository.delete(refreshToken);
    }

    private String generateAndSaveRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(jwtService.generateRefreshToken(user))
                .expiresAt(LocalDateTime.now().plusSeconds(REFRESH_TOKEN_EXPIRATION_TIME / 1000))
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken).getToken();
    }
}
