package com.taskapp.comment;

import com.taskapp.BaseIT;
import com.taskapp.board.dto.request.BoardRequest;
import com.taskapp.board.dto.response.BoardResponse;
import com.taskapp.boardtask.dto.request.BoardTaskRequest;
import com.taskapp.boardtask.dto.response.BoardTaskResponse;
import com.taskapp.boardtask.entity.enums.BoardTaskPriority;
import com.taskapp.comment.dto.request.CommentRequest;
import com.taskapp.comment.dto.response.CommentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class CommentControllerTest extends BaseIT {

    private static final String EMAIL = "comment-user@test.com";
    private static final String PASSWORD = "123456";

    @Test
    void createComment_shouldReturn201() {
        var headers = authHeader(EMAIL, PASSWORD);
        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Test Board", null)).retrieve().toEntity(BoardResponse.class).getBody();
        var task = restClient.post().uri("/api/v1/boards/" + board.id() + "/tasks").headers(h -> h.addAll(headers))
                .body(new BoardTaskRequest(board.id(), null, "Task", null, BoardTaskPriority.MEDIUM, null))
                .retrieve().toEntity(BoardTaskResponse.class).getBody();

        var response = restClient.post().uri("/api/v1/tasks/" + task.id() + "/comments")
                .headers(h -> h.addAll(headers)).body(new CommentRequest("Test comment"))
                .retrieve().toEntity(CommentResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().content()).isEqualTo("Test comment");
    }

    @Test
    void deleteComment_shouldReturn204() {
        var headers = authHeader("del-comment@test.com", PASSWORD);
        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Test Board", null)).retrieve().toEntity(BoardResponse.class).getBody();
        var task = restClient.post().uri("/api/v1/boards/" + board.id() + "/tasks").headers(h -> h.addAll(headers))
                .body(new BoardTaskRequest(board.id(), null, "Task", null, BoardTaskPriority.MEDIUM, null))
                .retrieve().toEntity(BoardTaskResponse.class).getBody();
        var comment = restClient.post().uri("/api/v1/tasks/" + task.id() + "/comments").headers(h -> h.addAll(headers))
                .body(new CommentRequest("To delete")).retrieve().toEntity(CommentResponse.class).getBody();

        var response = restClient.delete().uri("/api/v1/tasks/" + task.id() + "/comments/" + comment.id())
                .headers(h -> h.addAll(headers)).retrieve().toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
