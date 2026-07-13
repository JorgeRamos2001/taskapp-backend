package com.taskapp.comment;

import com.taskapp.comment.dto.request.CommentRequest;
import com.taskapp.comment.dto.response.CommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tasks/{taskId}/comments")
@RequiredArgsConstructor
class CommentController {

    private final CommentService commentService;

    @PostMapping
    ResponseEntity<CommentResponse> createComment(@PathVariable UUID taskId, @RequestBody @Valid CommentRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.create(authentication.getName(), taskId, request));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteComment(@PathVariable UUID taskId, @PathVariable UUID id, Authentication authentication) {
        commentService.delete(authentication.getName(), taskId, id);
        return ResponseEntity.noContent().build();
    }
}
