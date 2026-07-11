package com.taskapp.service;

import com.taskapp.dto.request.CommentRequest;
import com.taskapp.dto.response.CommentResponse;

import java.util.UUID;

public interface CommentService {
    CommentResponse create(String email, UUID boardTaskId, CommentRequest request);
    void delete(String email, UUID boardTaskId, UUID commentId);
}
