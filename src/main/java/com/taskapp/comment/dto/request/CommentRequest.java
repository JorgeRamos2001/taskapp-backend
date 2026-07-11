package com.taskapp.comment.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentRequest(
        @NotBlank( message = "Content cannot be null or empty")
        String content
) {
}
