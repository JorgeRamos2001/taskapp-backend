package com.taskapp.boardtask;

import com.taskapp.BaseIT;
import com.taskapp.board.dto.request.AddBoardMember;
import com.taskapp.board.dto.request.BoardRequest;
import com.taskapp.board.dto.response.BoardResponse;
import com.taskapp.board.entity.enums.BoardMemberRole;
import com.taskapp.boardtask.dto.request.AssignBoardTaskRequest;
import com.taskapp.boardtask.dto.request.BoardTaskRequest;
import com.taskapp.boardtask.dto.request.UpdateBoardTaskRequest;
import com.taskapp.boardtask.dto.response.BoardTaskResponse;
import com.taskapp.boardtask.entity.enums.BoardTaskPriority;
import com.taskapp.boardtask.entity.enums.BoardTaskState;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BoardTaskControllerTest extends BaseIT {

    private static final String EMAIL = "bt-owner@test.com";
    private static final String PASSWORD = "123456";

    @Test
    void createTask_shouldReturn201() {
        var headers = authHeader(EMAIL, PASSWORD);
        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Task Board", null)).retrieve().toEntity(BoardResponse.class).getBody();

        var response = restClient.post().uri("/api/v1/boards/" + board.id() + "/tasks")
                .headers(h -> h.addAll(headers))
                .body(new BoardTaskRequest(board.id(), null, "Task 1", "Desc", BoardTaskPriority.HIGH, null))
                .retrieve().toEntity(BoardTaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().title()).isEqualTo("Task 1");
    }

    @Test
    void getBoardTasks_shouldReturn200() {
        var headers = authHeader("bt-list@test.com", PASSWORD);
        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Task Board", null)).retrieve().toEntity(BoardResponse.class).getBody();

        var response = restClient.get().uri("/api/v1/boards/" + board.id() + "/tasks")
                .headers(h -> h.addAll(headers)).retrieve()
                .toEntity(new ParameterizedTypeReference<List<BoardTaskResponse>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void updateTask_shouldReturn200() {
        var headers = authHeader("bt-upd@test.com", PASSWORD);
        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Task Board", null)).retrieve().toEntity(BoardResponse.class).getBody();
        var task = restClient.post().uri("/api/v1/boards/" + board.id() + "/tasks").headers(h -> h.addAll(headers))
                .body(new BoardTaskRequest(board.id(), null, "Original", null, BoardTaskPriority.MEDIUM, null))
                .retrieve().toEntity(BoardTaskResponse.class).getBody();

        var response = restClient.put().uri("/api/v1/tasks/" + task.id()).headers(h -> h.addAll(headers))
                .body(new UpdateBoardTaskRequest(board.id(), "Updated", "New desc", BoardTaskPriority.HIGH, BoardTaskState.IN_PROGRESS, null))
                .retrieve().toEntity(BoardTaskResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().title()).isEqualTo("Updated");
    }

    @Test
    void deleteTask_shouldReturn204() {
        var headers = authHeader("bt-del@test.com", PASSWORD);
        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Task Board", null)).retrieve().toEntity(BoardResponse.class).getBody();
        var task = restClient.post().uri("/api/v1/boards/" + board.id() + "/tasks").headers(h -> h.addAll(headers))
                .body(new BoardTaskRequest(board.id(), null, "To Delete", null, BoardTaskPriority.LOW, null))
                .retrieve().toEntity(BoardTaskResponse.class).getBody();

        var response = restClient.delete().uri("/api/v1/tasks/" + task.id()).headers(h -> h.addAll(headers))
                .retrieve().toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void assignAndUnassignTask_shouldReturn200() {
        var ownerHeaders = authHeader("assign-owner@test.com", PASSWORD);
        var memberHeaders = authHeader("assign-user@test.com", PASSWORD);

        var memberBody = restClient.get().uri("/api/v1/users/me").headers(h -> h.addAll(memberHeaders))
                .retrieve().toEntity(String.class).getBody();
        int start = memberBody.indexOf("\"id\":\"") + 6;
        var assigneeId = UUID.fromString(memberBody.substring(start, memberBody.indexOf("\"", start)));

        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(ownerHeaders))
                .body(new BoardRequest("Shared", null)).retrieve().toEntity(BoardResponse.class).getBody();
        restClient.post().uri("/api/v1/boards/" + board.id() + "/members").headers(h -> h.addAll(ownerHeaders))
                .body(new AddBoardMember(assigneeId, BoardMemberRole.MEMBER)).retrieve().toBodilessEntity();
        var task = restClient.post().uri("/api/v1/boards/" + board.id() + "/tasks").headers(h -> h.addAll(ownerHeaders))
                .body(new BoardTaskRequest(board.id(), null, "Assignable", null, BoardTaskPriority.MEDIUM, null))
                .retrieve().toEntity(BoardTaskResponse.class).getBody();

        var assign = restClient.post().uri("/api/v1/tasks/" + task.id() + "/assign").headers(h -> h.addAll(ownerHeaders))
                .body(new AssignBoardTaskRequest(board.id(), assigneeId)).retrieve().toEntity(BoardTaskResponse.class);
        assertThat(assign.getStatusCode()).isEqualTo(HttpStatus.OK);

        var unassign = restClient.delete().uri("/api/v1/tasks/" + task.id() + "/assign")
                .headers(h -> h.addAll(ownerHeaders)).retrieve().toEntity(BoardTaskResponse.class);
        assertThat(unassign.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
