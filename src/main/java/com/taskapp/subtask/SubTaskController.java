package com.taskapp.subtask;

import com.taskapp.subtask.dto.request.SubTaskRequest;
import com.taskapp.subtask.dto.response.SubTaskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
class SubTaskController {

    private final SubTaskService subTaskService;

    @PostMapping("/tasks/{taskId}/subtasks")
    ResponseEntity<SubTaskResponse> createSubTask(@PathVariable UUID taskId, @RequestBody @Valid SubTaskRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subTaskService.create(authentication.getName(), taskId, request));
    }

    @PutMapping("/subtasks/{id}/complete")
    ResponseEntity<SubTaskResponse> completeSubTask(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(subTaskService.complete(authentication.getName(), null, id));
    }

    @DeleteMapping("/subtasks/{id}")
    ResponseEntity<Void> deleteSubTask(@PathVariable UUID id, Authentication authentication) {
        subTaskService.delete(authentication.getName(), null, id);
        return ResponseEntity.noContent().build();
    }
}
