package com.taskapp.boardtask;

import com.taskapp.boardtask.dto.request.AssignBoardTaskRequest;
import com.taskapp.boardtask.dto.request.BoardTaskRequest;
import com.taskapp.boardtask.dto.request.UpdateBoardTaskRequest;
import com.taskapp.boardtask.dto.response.BoardTaskResponse;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
class BoardTaskController {

    private final BoardTaskService boardTaskService;

    @PostMapping("/boards/{boardId}/tasks")
    ResponseEntity<BoardTaskResponse> createTask(@PathVariable UUID boardId, @RequestBody @Valid BoardTaskRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(boardTaskService.create(authentication.getName(), request));
    }

    @GetMapping("/boards/{boardId}/tasks")
    ResponseEntity<List<BoardTaskResponse>> getBoardTasks(@PathVariable UUID boardId, Authentication authentication) {
        return ResponseEntity.ok(boardTaskService.getBoardTasks(authentication.getName(), boardId));
    }

    @GetMapping("/tasks/{id}")
    ResponseEntity<BoardTaskResponse> getTask(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(boardTaskService.getBoardTask(authentication.getName(), id));
    }

    @PutMapping("/tasks/{id}")
    ResponseEntity<BoardTaskResponse> updateTask(@PathVariable UUID id, @RequestBody @Valid UpdateBoardTaskRequest request, Authentication authentication) {
        return ResponseEntity.ok(boardTaskService.update(id, authentication.getName(), request));
    }

    @DeleteMapping("/tasks/{id}")
    ResponseEntity<Void> deleteTask(@PathVariable UUID id, Authentication authentication) {
        boardTaskService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tasks/{id}/assign")
    ResponseEntity<BoardTaskResponse> assignTask(@PathVariable UUID id, @RequestBody @Valid AssignBoardTaskRequest request, Authentication authentication) {
        boardTaskService.assignTask(id, authentication.getName(), request);
        return ResponseEntity.ok(boardTaskService.getBoardTask(authentication.getName(), id));
    }

    @DeleteMapping("/tasks/{id}/assign")
    ResponseEntity<BoardTaskResponse> unassignTask(@PathVariable UUID id, Authentication authentication) {
        boardTaskService.unassignTask(id, authentication.getName());
        return ResponseEntity.ok(boardTaskService.getBoardTask(authentication.getName(), id));
    }
}
