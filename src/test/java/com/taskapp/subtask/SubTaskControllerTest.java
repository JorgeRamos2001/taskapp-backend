package com.taskapp.subtask;

import com.taskapp.BaseIT;
import com.taskapp.board.dto.request.BoardRequest;
import com.taskapp.board.dto.response.BoardResponse;
import com.taskapp.boardtask.dto.request.BoardTaskRequest;
import com.taskapp.boardtask.dto.response.BoardTaskResponse;
import com.taskapp.boardtask.entity.enums.BoardTaskPriority;
import com.taskapp.subtask.dto.request.SubTaskRequest;
import com.taskapp.subtask.dto.response.SubTaskResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class SubTaskControllerTest extends BaseIT {

    private static final String EMAIL = "st-user@test.com";
    private static final String PASSWORD = "123456";

    @Test
    void createSubTask_shouldReturn201() {
        var headers = authHeader(EMAIL, PASSWORD);
        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Test Board", null)).retrieve().toEntity(BoardResponse.class).getBody();
        var task = restClient.post().uri("/api/v1/boards/" + board.id() + "/tasks").headers(h -> h.addAll(headers))
                .body(new BoardTaskRequest(board.id(), null, "Task", null, BoardTaskPriority.MEDIUM, null))
                .retrieve().toEntity(BoardTaskResponse.class).getBody();

        var response = restClient.post().uri("/api/v1/tasks/" + task.id() + "/subtasks")
                .headers(h -> h.addAll(headers)).body(new SubTaskRequest("Sub Task 1"))
                .retrieve().toEntity(SubTaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().title()).isEqualTo("Sub Task 1");
    }

    @Test
    void completeSubTask_shouldReturn200() {
        var headers = authHeader("complete-st@test.com", PASSWORD);
        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Test Board", null)).retrieve().toEntity(BoardResponse.class).getBody();
        var task = restClient.post().uri("/api/v1/boards/" + board.id() + "/tasks").headers(h -> h.addAll(headers))
                .body(new BoardTaskRequest(board.id(), null, "Task", null, BoardTaskPriority.MEDIUM, null))
                .retrieve().toEntity(BoardTaskResponse.class).getBody();
        var subTask = restClient.post().uri("/api/v1/tasks/" + task.id() + "/subtasks").headers(h -> h.addAll(headers))
                .body(new SubTaskRequest("To Complete")).retrieve().toEntity(SubTaskResponse.class).getBody();

        var response = restClient.put().uri("/api/v1/subtasks/" + subTask.id() + "/complete")
                .headers(h -> h.addAll(headers)).retrieve().toEntity(SubTaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().completed()).isTrue();
    }

    @Test
    void deleteSubTask_shouldReturn204() {
        var headers = authHeader("delete-st@test.com", PASSWORD);
        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Test Board", null)).retrieve().toEntity(BoardResponse.class).getBody();
        var task = restClient.post().uri("/api/v1/boards/" + board.id() + "/tasks").headers(h -> h.addAll(headers))
                .body(new BoardTaskRequest(board.id(), null, "Task", null, BoardTaskPriority.MEDIUM, null))
                .retrieve().toEntity(BoardTaskResponse.class).getBody();
        var subTask = restClient.post().uri("/api/v1/tasks/" + task.id() + "/subtasks").headers(h -> h.addAll(headers))
                .body(new SubTaskRequest("To Delete")).retrieve().toEntity(SubTaskResponse.class).getBody();

        var response = restClient.delete().uri("/api/v1/subtasks/" + subTask.id())
                .headers(h -> h.addAll(headers)).retrieve().toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
