package com.taskapp.board;

import com.taskapp.board.dto.request.AddBoardMember;
import com.taskapp.board.dto.request.BoardRequest;
import com.taskapp.board.dto.request.RemoveBoardMember;
import com.taskapp.board.dto.response.BoardResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
class BoardController {

    private final BoardService boardService;

    @PostMapping
    ResponseEntity<BoardResponse> createBoard(@RequestBody @Valid BoardRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardService.createBoard(request, authentication.getName()));
    }

    @GetMapping("my-boards")
    ResponseEntity<List<BoardResponse>> getMyBoards(Authentication authentication) {
        return ResponseEntity.ok(boardService.getMyBoards(authentication.getName()));
    }

    @GetMapping()
    ResponseEntity<List<BoardResponse>> getAllAccessibleBoards(Authentication authentication) {
        return ResponseEntity.ok(boardService.getAllBoards(authentication.getName()));
    }

    @GetMapping("/{id}")
    ResponseEntity<BoardResponse> getBoard(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(boardService.getBoard(authentication.getName(), id));
    }

    @PutMapping("/{id}")
    ResponseEntity<BoardResponse> updateBoard(@PathVariable UUID id, @RequestBody @Valid BoardRequest request, Authentication authentication) {
        return ResponseEntity.ok(boardService.updateBoard(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteBoard(@PathVariable UUID id, Authentication authentication) {
        boardService.deleteBoard(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members")
    ResponseEntity<Void> addMember(@PathVariable UUID id, @RequestBody @Valid AddBoardMember request, Authentication authentication) {
        boardService.addMember(id, request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/members/{userId}")
    ResponseEntity<Void> removeMember(@PathVariable UUID id, @PathVariable UUID userId, Authentication authentication) {
        boardService.removeMember(id, new RemoveBoardMember(userId), authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
