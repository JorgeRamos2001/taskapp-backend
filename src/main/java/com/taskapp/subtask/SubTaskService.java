package com.taskapp.subtask;

import com.taskapp.subtask.dto.request.SubTaskRequest;
import com.taskapp.subtask.dto.response.SubTaskResponse;

import java.util.UUID;

public interface SubTaskService {
    SubTaskResponse create(String email, UUID taskId, SubTaskRequest request);
    SubTaskResponse complete(String email, UUID taskId, UUID subTaskId);
    void delete(String email, UUID taskId, UUID subTaskId);
}
