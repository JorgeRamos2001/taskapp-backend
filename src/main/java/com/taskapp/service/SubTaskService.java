package com.taskapp.service;

import com.taskapp.dto.request.SubTaskRequest;
import com.taskapp.dto.response.SubTaskResponse;

import java.util.UUID;

public interface SubTaskService {
    SubTaskResponse create(String email, UUID taskId, SubTaskRequest request);
    SubTaskResponse complete(String email, UUID taskId, UUID subTaskId);
    void delete(String email, UUID taskId, UUID subTaskId);
}
