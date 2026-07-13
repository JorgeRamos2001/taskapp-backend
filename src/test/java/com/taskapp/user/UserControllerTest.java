package com.taskapp.user;

import com.taskapp.BaseIT;
import com.taskapp.user.dto.request.ChangePasswordRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class UserControllerTest extends BaseIT {

    private static final String EMAIL = "user-me@test.com";
    private static final String PASSWORD = "123456";

    @Test
    void getMe_shouldReturn200() {
        var headers = authHeader(EMAIL, PASSWORD);

        var response = restClient.get()
                .uri("/api/v1/users/me")
                .headers(h -> h.addAll(headers))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("email");
    }

    @Test
    void getMe_shouldReturn401_whenNoAuth() {
        var response = restClient.get()
                .uri("/api/v1/users/me")
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void changePassword_shouldReturn200() {
        var headers = authHeader(EMAIL, PASSWORD);

        var response = restClient.put()
                .uri("/api/v1/users/password")
                .headers(h -> h.addAll(headers))
                .body(new ChangePasswordRequest(PASSWORD, "654321"))
                .retrieve()
                .toEntity(String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void changePassword_shouldReturn400_whenWrongOldPassword() {
        var headers = authHeader(EMAIL, PASSWORD);

        var response = restClient.put()
                .uri("/api/v1/users/password")
                .headers(h -> h.addAll(headers))
                .body(new ChangePasswordRequest("wrong-old", "654321"))
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void changePassword_shouldReturn400_whenInvalidData() {
        var headers = authHeader(EMAIL, PASSWORD);

        var response = restClient.put()
                .uri("/api/v1/users/password")
                .headers(h -> h.addAll(headers))
                .body(new ChangePasswordRequest("", ""))
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
