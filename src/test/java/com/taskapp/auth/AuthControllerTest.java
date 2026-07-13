package com.taskapp.auth;

import com.taskapp.BaseIT;
import com.taskapp.auth.dto.request.LoginRequest;
import com.taskapp.auth.dto.request.LogoutRequest;
import com.taskapp.auth.dto.request.RefreshTokenRequest;
import com.taskapp.auth.dto.request.RegisterRequest;
import com.taskapp.auth.dto.response.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

class AuthControllerTest extends BaseIT {

    @Test
    void register_shouldReturn201() {
        var response = restClient.post()
                .uri("/api/v1/auth/register")
                .body(new RegisterRequest("Test", "test@test.com", "123456"))
                .retrieve()
                .toEntity(AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotEmpty();
        assertThat(response.getBody().refreshToken()).isNotEmpty();
        assertThat(response.getBody().tokenType()).isEqualTo("Bearer");
    }

    @Test
    void register_shouldReturn409_whenEmailExists() {
        restClient.post()
                .uri("/api/v1/auth/register")
                .body(new RegisterRequest("Test", "dup@test.com", "123456"))
                .retrieve()
                .toEntity(AuthResponse.class);

        var response = restClient.post()
                .uri("/api/v1/auth/register")
                .body(new RegisterRequest("Test", "dup@test.com", "123456"))
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void register_shouldReturn400_whenInvalidData() {
        var response = restClient.post()
                .uri("/api/v1/auth/register")
                .body(new RegisterRequest("", "invalid", "12"))
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void login_shouldReturn200() {
        restClient.post()
                .uri("/api/v1/auth/register")
                .body(new RegisterRequest("Test", "login@test.com", "123456"))
                .retrieve()
                .toEntity(AuthResponse.class);

        var response = restClient.post()
                .uri("/api/v1/auth/login")
                .body(new LoginRequest("login@test.com", "123456"))
                .retrieve()
                .toEntity(AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotEmpty();
        assertThat(response.getBody().refreshToken()).isNotEmpty();
    }

    @Test
    void login_shouldReturn401_whenBadCredentials() {
        var response = restClient.post()
                .uri("/api/v1/auth/login")
                .body(new LoginRequest("nonexist@test.com", "wrong123"))
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void login_shouldReturn400_whenInvalidData() {
        var response = restClient.post()
                .uri("/api/v1/auth/login")
                .body(new LoginRequest("", ""))
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void refresh_shouldReturn200() {
        var auth = restClient.post()
                .uri("/api/v1/auth/register")
                .body(new RegisterRequest("Test", "refresh@test.com", "123456"))
                .retrieve()
                .toEntity(AuthResponse.class).getBody();

        var response = restClient.post()
                .uri("/api/v1/auth/refresh")
                .body(new RefreshTokenRequest(auth.refreshToken()))
                .retrieve()
                .toEntity(AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().accessToken()).isNotEmpty();
        assertThat(response.getBody().refreshToken()).isEqualTo(auth.refreshToken());
    }

    @Test
    void refresh_shouldReturn400_whenInvalidToken() {
        var response = restClient.post()
                .uri("/api/v1/auth/refresh")
                .body(new RefreshTokenRequest("invalid-token"))
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void logout_shouldReturn204() {
        var auth = restClient.post()
                .uri("/api/v1/auth/register")
                .body(new RegisterRequest("Test", "logout@test.com", "123456"))
                .retrieve()
                .toEntity(AuthResponse.class).getBody();

        var response = restClient.post()
                .uri("/api/v1/auth/logout")
                .header("Content-Type", "application/json")
                .body(new LogoutRequest(auth.refreshToken()))
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
