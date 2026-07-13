package com.taskapp.board;

import com.taskapp.BaseIT;
import com.taskapp.board.dto.request.AddBoardMember;
import com.taskapp.board.dto.request.BoardRequest;
import com.taskapp.board.dto.response.BoardResponse;
import com.taskapp.board.entity.enums.BoardMemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BoardControllerTest extends BaseIT {

    private static final String OWNER_EMAIL = "board-owner@test.com";
    private static final String PASSWORD = "123456";

    @Test
    void createBoard_shouldReturn201() {
        var headers = authHeader(OWNER_EMAIL, PASSWORD);

        var response = restClient.post()
                .uri("/api/v1/boards")
                .headers(h -> h.addAll(headers))
                .body(new BoardRequest("My Board", "Description"))
                .retrieve()
                .toEntity(BoardResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().title()).isEqualTo("My Board");
    }

    @Test
    void createBoard_shouldReturn400_whenNoTitle() {
        var headers = authHeader("no-title@test.com", PASSWORD);

        var response = restClient.post()
                .uri("/api/v1/boards")
                .headers(h -> h.addAll(headers))
                .body(new BoardRequest("", null))
                .retrieve()
                .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getMyBoards_shouldReturn200() {
        var headers = authHeader("my-boards@test.com", PASSWORD);

        restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Board 1", null)).retrieve().toEntity(BoardResponse.class);
        restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Board 2", null)).retrieve().toEntity(BoardResponse.class);

        var response = restClient.get().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .retrieve().toEntity(new ParameterizedTypeReference<List<BoardResponse>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getBoard_shouldReturn200() {
        var headers = authHeader("get-board@test.com", PASSWORD);

        var created = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("My Board", "Desc")).retrieve().toEntity(BoardResponse.class).getBody();

        var response = restClient.get().uri("/api/v1/boards/" + created.id()).headers(h -> h.addAll(headers))
                .retrieve().toEntity(BoardResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().title()).isEqualTo("My Board");
    }

    @Test
    void getBoard_shouldReturn404_whenNotFound() {
        var headers = authHeader("board-404@test.com", PASSWORD);

        var response = restClient.get().uri("/api/v1/boards/" + UUID.randomUUID())
                .headers(h -> h.addAll(headers)).retrieve().toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateBoard_shouldReturn200() {
        var headers = authHeader("upd-board@test.com", PASSWORD);

        var created = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("Original", null)).retrieve().toEntity(BoardResponse.class).getBody();

        var response = restClient.put().uri("/api/v1/boards/" + created.id()).headers(h -> h.addAll(headers))
                .body(new BoardRequest("Updated", "New desc")).retrieve().toEntity(BoardResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().title()).isEqualTo("Updated");
    }

    @Test
    void deleteBoard_shouldReturn204() {
        var headers = authHeader("del-board@test.com", PASSWORD);

        var created = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(headers))
                .body(new BoardRequest("To Delete", null)).retrieve().toEntity(BoardResponse.class).getBody();

        var response = restClient.delete().uri("/api/v1/boards/" + created.id()).headers(h -> h.addAll(headers))
                .retrieve().toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void addMember_shouldReturn201() {
        var ownerHeaders = authHeader(OWNER_EMAIL, PASSWORD);
        var memberHeaders = authHeader("add-member@test.com", PASSWORD);

        var memberBody = restClient.get().uri("/api/v1/users/me").headers(h -> h.addAll(memberHeaders))
                .retrieve().toEntity(String.class).getBody();
        var memberId = extractId(memberBody);

        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(ownerHeaders))
                .body(new BoardRequest("Shared", null)).retrieve().toEntity(BoardResponse.class).getBody();

        var response = restClient.post().uri("/api/v1/boards/" + board.id() + "/members")
                .headers(h -> h.addAll(ownerHeaders))
                .body(new AddBoardMember(memberId, BoardMemberRole.MEMBER))
                .retrieve().toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void removeMember_shouldReturn204() {
        var ownerHeaders = authHeader("rm-owner@test.com", PASSWORD);
        var memberHeaders = authHeader("rm-member2@test.com", PASSWORD);

        var memberBody = restClient.get().uri("/api/v1/users/me").headers(h -> h.addAll(memberHeaders))
                .retrieve().toEntity(String.class).getBody();
        var memberId = extractId(memberBody);

        var board = restClient.post().uri("/api/v1/boards").headers(h -> h.addAll(ownerHeaders))
                .body(new BoardRequest("Shared", null)).retrieve().toEntity(BoardResponse.class).getBody();

        restClient.post().uri("/api/v1/boards/" + board.id() + "/members").headers(h -> h.addAll(ownerHeaders))
                .body(new AddBoardMember(memberId, BoardMemberRole.MEMBER)).retrieve().toBodilessEntity();

        var response = restClient.delete().uri("/api/v1/boards/" + board.id() + "/members/" + memberId)
                .headers(h -> h.addAll(ownerHeaders)).retrieve().toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private UUID extractId(String json) {
        int start = json.indexOf("\"id\":\"") + 6;
        return UUID.fromString(json.substring(start, json.indexOf("\"", start)));
    }
}
