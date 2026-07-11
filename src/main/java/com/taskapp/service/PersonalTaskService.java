package com.taskapp.service;

import com.taskapp.dto.request.PersonalTaskRequest;
import com.taskapp.dto.request.UpdatePersonalTaskRequest;
import com.taskapp.dto.response.PersonalTaskResponse;

import java.util.List;
import java.util.UUID;

public interface PersonalTaskService {
    PersonalTaskResponse create(PersonalTaskRequest request, String email);
    PersonalTaskResponse getPersonalTask(String email, UUID taskId);
    List<PersonalTaskResponse> getPersonalTasks(String email);
    PersonalTaskResponse update(UUID personalTaskId, UpdatePersonalTaskRequest request, String email);
    void delete(String email, UUID taskId);
}
