package com.taskapp.subtask;

import com.taskapp.auth.entity.User;
import com.taskapp.auth.repository.UserRepository;
import com.taskapp.boardtask.entity.BoardTask;
import com.taskapp.boardtask.repository.BoardTaskRepository;
import com.taskapp.shared.exception.EntityNotFoundException;
import com.taskapp.shared.exception.StateTransitionException;
import com.taskapp.shared.exception.ValidationException;
import com.taskapp.subtask.dto.request.SubTaskRequest;
import com.taskapp.subtask.dto.response.SubTaskResponse;
import com.taskapp.subtask.entity.SubTask;
import com.taskapp.subtask.repository.SubTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubTaskServiceImpl implements SubTaskService {
    private final SubTaskRepository subTaskRepository;
    private final BoardTaskRepository boardTaskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SubTaskResponse create(String email, UUID taskId, SubTaskRequest request) {
        log.warn("Creating sub task for user: {}", email);
        BoardTask boardTask = boardTaskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Board task not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        if (!user.getId().equals(boardTask.getAssignee().getId()) && boardTask.getBoard().getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }
        SubTask subTask = SubTask.builder()
                .boardTask(boardTask)
                .title(request.title())
                .completed(false)
                .build();
        SubTask savedSubTask = subTaskRepository.save(subTask);
        return convertToResponse(savedSubTask);
    }

    @Override
    @Transactional
    public SubTaskResponse complete(String email, UUID taskId, UUID subTaskId) {
        log.warn("Completing sub task for user: {}", email);
        SubTask subTask = subTaskRepository.findById(subTaskId).orElseThrow(() -> new EntityNotFoundException("Sub task not found."));
        BoardTask boardTask = subTask.getBoardTask();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!user.getId().equals(boardTask.getAssignee().getId()) && boardTask.getBoard().getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }
        if (Boolean.TRUE.equals(subTask.getCompleted())) {
            throw new StateTransitionException("Sub task is already completed.");
        }
        subTask.setCompleted(true);
        SubTask savedSubTask = subTaskRepository.save(subTask);
        return convertToResponse(savedSubTask);
    }

    @Override
    @Transactional
    public void delete(String email, UUID taskId, UUID subTaskId) {
        log.warn("Deleting sub task for user: {}", email);
        SubTask subTask = subTaskRepository.findById(subTaskId).orElseThrow(() -> new EntityNotFoundException("Sub task not found."));
        BoardTask boardTask = subTask.getBoardTask();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!user.getId().equals(boardTask.getAssignee().getId()) && boardTask.getBoard().getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }
        subTaskRepository.deleteById(subTaskId);
    }

    private SubTaskResponse convertToResponse(SubTask subTask) {
        return new SubTaskResponse(
                subTask.getId(),
                subTask.getBoardTask().getId(),
                subTask.getTitle(),
                subTask.getCompleted()
        );
    }
}
