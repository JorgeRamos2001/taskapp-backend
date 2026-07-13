package com.taskapp.personaltask;

import com.taskapp.BaseIT;
import com.taskapp.personaltask.dto.request.PersonalTaskRequest;
import com.taskapp.personaltask.dto.request.UpdatePersonalTaskRequest;
import com.taskapp.personaltask.dto.response.PersonalTaskResponse;
import com.taskapp.personaltask.entity.enums.PersonalTaskPriority;
import com.taskapp.personaltask.entity.enums.PersonalTaskState;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalTaskControllerTest extends BaseIT {

    private static final String EMAIL = "pt-user@test.com";
    private static final String PASSWORD = "123456";

    @Test
    void createPersonalTask_shouldReturn201() {
        var headers = authHeader(EMAIL, PASSWORD);

        var response = restClient.post().uri("/api/v1/personal-tasks").headers(h -> h.addAll(headers))
                .body(new PersonalTaskRequest("My Task", "Desc", PersonalTaskPriority.HIGH, null))
                .retrieve().toEntity(PersonalTaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().title()).isEqualTo("My Task");
        assertThat(response.getBody().state()).isEqualTo(PersonalTaskState.IN_PROGRESS);
    }

    @Test
    void getPersonalTasks_shouldReturn200() {
        var headers = authHeader("pt-list@test.com", PASSWORD);

        restClient.post().uri("/api/v1/personal-tasks").headers(h -> h.addAll(headers))
                .body(new PersonalTaskRequest("Task 1", null, PersonalTaskPriority.MID, null))
                .retrieve().toEntity(PersonalTaskResponse.class);
        restClient.post().uri("/api/v1/personal-tasks").headers(h -> h.addAll(headers))
                .body(new PersonalTaskRequest("Task 2", null, PersonalTaskPriority.LOW, null))
                .retrieve().toEntity(PersonalTaskResponse.class);

        var response = restClient.get().uri("/api/v1/personal-tasks").headers(h -> h.addAll(headers))
                .retrieve().toEntity(new ParameterizedTypeReference<List<PersonalTaskResponse>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getPersonalTask_shouldReturn200() {
        var headers = authHeader("pt-get@test.com", PASSWORD);

        var created = restClient.post().uri("/api/v1/personal-tasks").headers(h -> h.addAll(headers))
                .body(new PersonalTaskRequest("Specific", null, PersonalTaskPriority.HIGH, null))
                .retrieve().toEntity(PersonalTaskResponse.class).getBody();

        var response = restClient.get().uri("/api/v1/personal-tasks/" + created.id())
                .headers(h -> h.addAll(headers)).retrieve().toEntity(PersonalTaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().title()).isEqualTo("Specific");
    }

    @Test
    void updatePersonalTask_shouldReturn200() {
        var headers = authHeader("pt-upd@test.com", PASSWORD);

        var created = restClient.post().uri("/api/v1/personal-tasks").headers(h -> h.addAll(headers))
                .body(new PersonalTaskRequest("Original", null, PersonalTaskPriority.MID, null))
                .retrieve().toEntity(PersonalTaskResponse.class).getBody();

        var response = restClient.put().uri("/api/v1/personal-tasks/" + created.id()).headers(h -> h.addAll(headers))
                .body(new UpdatePersonalTaskRequest("Updated", "New desc", PersonalTaskPriority.HIGH, PersonalTaskState.DONE, null))
                .retrieve().toEntity(PersonalTaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().title()).isEqualTo("Updated");
        assertThat(response.getBody().state()).isEqualTo(PersonalTaskState.DONE);
    }

    @Test
    void deletePersonalTask_shouldReturn204() {
        var headers = authHeader("pt-del@test.com", PASSWORD);

        var created = restClient.post().uri("/api/v1/personal-tasks").headers(h -> h.addAll(headers))
                .body(new PersonalTaskRequest("To Delete", null, PersonalTaskPriority.LOW, null))
                .retrieve().toEntity(PersonalTaskResponse.class).getBody();

        var response = restClient.delete().uri("/api/v1/personal-tasks/" + created.id())
                .headers(h -> h.addAll(headers)).retrieve().toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void createPersonalTask_shouldReturn400_whenNoTitle() {
        var headers = authHeader("pt-inv@test.com", PASSWORD);

        var response = restClient.post().uri("/api/v1/personal-tasks").headers(h -> h.addAll(headers))
                .body(new PersonalTaskRequest("", null, null, null))
                .retrieve().toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
