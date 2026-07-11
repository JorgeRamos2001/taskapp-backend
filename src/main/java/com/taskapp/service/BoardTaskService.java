package com.taskapp.service;

import com.taskapp.dto.request.AssignBoardTaskRequest;
import com.taskapp.dto.request.BoardTaskRequest;
import com.taskapp.dto.request.UpdateBoardTaskRequest;
import com.taskapp.dto.response.BoardTaskResponse;

import java.util.List;
import java.util.UUID;

public interface BoardTaskService {
    BoardTaskResponse create(String email, BoardTaskRequest request);
    BoardTaskResponse getBoardTask(String email, UUID taskId);
    List<BoardTaskResponse> getBoardTasks(String email, UUID boardId);
    BoardTaskResponse update(UUID taskId, String email, UpdateBoardTaskRequest request);
    void delete(String email, UUID taskId);

    void assignTask(UUID taskId, String email, AssignBoardTaskRequest request);
    void unassignTask(UUID taskId, String email);


}
