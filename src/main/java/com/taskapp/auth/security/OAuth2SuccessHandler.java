package com.taskapp.auth.security;

import com.taskapp.auth.entity.RefreshToken;
import com.taskapp.auth.entity.User;
import com.taskapp.auth.entity.enums.UserProvider;
import com.taskapp.auth.repository.RefreshTokenRepository;
import com.taskapp.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");
        String avatar = (String) attributes.get("picture");

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = User.builder()
                    .email(email)
                    .name(name)
                    .urlAvatar(avatar)
                    .provider(UserProvider.GOOGLE)
                    .password("")
                    .build();
            return userRepository.save(newUser);
        });

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.deleteByUserId(user.getId());
        RefreshToken tokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .expiresAt(LocalDateTime.now().plusSeconds(604800))
                .user(user)
                .build();
        refreshTokenRepository.save(tokenEntity);

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("access_token", accessToken)
                .queryParam("refresh_token", refreshToken)
                .build().toUriString();

        log.info("OAuth2 login successful for user: {}", email);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
