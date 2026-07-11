package com.taskapp.service.implementations;

import com.taskapp.dto.request.AssignBoardTaskRequest;
import com.taskapp.dto.request.BoardTaskRequest;
import com.taskapp.dto.request.UpdateBoardTaskRequest;
import com.taskapp.dto.response.BoardMemberResponse;
import com.taskapp.dto.response.BoardTaskResponse;
import com.taskapp.dto.response.CommentResponse;
import com.taskapp.dto.response.SubTaskResponse;
import com.taskapp.entity.Board;
import com.taskapp.entity.BoardTask;
import com.taskapp.entity.User;
import com.taskapp.entity.enums.BoardTaskState;
import com.taskapp.exception.EntityNotFoundException;
import com.taskapp.exception.ValidationException;
import com.taskapp.repository.BoardMemberRepository;
import com.taskapp.repository.BoardRepository;
import com.taskapp.repository.BoardTaskRepository;
import com.taskapp.repository.UserRepository;
import com.taskapp.service.BoardTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardTaskServiceImpl implements BoardTaskService {
    private final BoardTaskRepository boardTaskRepository;
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BoardTaskResponse create(String email, BoardTaskRequest request) {
        log.warn("Creating board task for user: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        Board board = boardRepository.findById(request.boardId()).orElseThrow(() -> new EntityNotFoundException("Board not found."));

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new ValidationException("User does not have access to this board.");
        }

        BoardTask boardTask = BoardTask.builder()
                .board(board)
                .title(request.title())
                .description(request.description())
                .priority(request.priority())
                .dueDate(request.dueDate())
                .build();

        if (request.assigneeId() != null) {
            User assignee = userRepository.findById(request.assigneeId()).orElseThrow(() -> new EntityNotFoundException("Assignee not found."));
            boardTask.setAssignee(assignee);
            boardTask.setState(BoardTaskState.IN_PROGRESS);
        } else {
            boardTask.setState(BoardTaskState.UNASSIGNED);
        }
        BoardTask savedTask = boardTaskRepository.save(boardTask);
        return convertToResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardTaskResponse getBoardTask(String email, UUID taskId) {
        log.warn("Getting board task for user: {}", email);
        BoardTask boardTask = boardTaskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Board task not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!user.getId().equals(boardTask.getBoard().getOwner().getId()) && boardTask.getBoard().getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }
        return convertToResponse(boardTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardTaskResponse> getBoardTasks(String email, UUID boardId) {
        log.warn("Getting board tasks for user: {}", email);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        if (!user.getId().equals(board.getOwner().getId()) && board.getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }
        return boardTaskRepository.findByBoardId(board.getId()).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional
    public BoardTaskResponse update(UUID taskId, String email, UpdateBoardTaskRequest request) {
        log.warn("Updating board task for user: {}", email);
        BoardTask boardTask = boardTaskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Board task not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        if (!user.getId().equals(boardTask.getBoard().getOwner().getId()) && boardTask.getBoard().getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }
        boardTask.setTitle(request.title());
        boardTask.setDescription(request.description());
        boardTask.setPriority(request.priority());
        boardTask.setState(request.state());
        boardTask.setDueDate(request.dueDate());
        BoardTask savedTask = boardTaskRepository.save(boardTask);
        return convertToResponse(savedTask);
    }

    @Override
    public void delete(String email, UUID taskId) {
        log.warn("Deleting board task for user: {}", email);
        BoardTask boardTask = boardTaskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Board task not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        if (!user.getId().equals(boardTask.getBoard().getOwner().getId()) && boardTask.getBoard().getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }
        boardTaskRepository.deleteById(taskId);
    }

    @Override
    public void assignTask(UUID taskId, String email, AssignBoardTaskRequest request) {
        log.warn("Assigning task for user: {}", email);
        BoardTask boardTask = boardTaskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Board task not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        if (!user.getId().equals(boardTask.getBoard().getOwner().getId()) && boardTask.getBoard().getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }
        User assign = userRepository.findById(request.userId()).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (assign.getId().equals(boardTask.getAssignee().getId())) {
            throw new ValidationException("User is already assigned to this task.");
        }
        boardTask.setAssignee(assign);
        boardTaskRepository.save(boardTask);
    }

    @Override
    public void unassignTask(UUID taskId, String email) {
        log.warn("Unassigning task for user: {}", email);
        BoardTask boardTask = boardTaskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Board task not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        if (!user.getId().equals(boardTask.getBoard().getOwner().getId()) && boardTask.getBoard().getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }
        User assignee = boardTask.getAssignee();
        if (assignee == null) {
            throw new ValidationException("Task is not assigned to anyone.");
        }
        boardTask.setAssignee(null);
        boardTaskRepository.save(boardTask);
    }

    private BoardTaskResponse convertToResponse(BoardTask boardTask) {
        BoardMemberResponse assignee = null;

        if (boardTask.getAssignee() != null) {
            assignee = new BoardMemberResponse(
                    boardTask.getAssignee().getId(),
                    boardTask.getAssignee().getName(),
                    boardTask.getAssignee().getEmail(),
                    boardTask.getAssignee().getUrlAvatar(),
                    boardMemberRepository.findByBoardIdAndUserId(boardTask.getBoard().getId(), boardTask.getAssignee().getId()).orElseThrow(() -> new EntityNotFoundException("Board member not found.")).getRole()
            );
        }

        return new BoardTaskResponse(
                boardTask.getId(),
                boardTask.getBoard().getId(),
                assignee,
                boardTask.getTitle(),
                boardTask.getDescription(),
                boardTask.getPriority(),
                boardTask.getState(),
                boardTask.getDueDate(),
                boardTask.getCreatedAt(),
                boardTask.getSubTasks().stream()
                        .map(subTask -> {
                            return new SubTaskResponse(
                                    subTask.getId(),
                                    subTask.getBoardTask().getId(),
                                    subTask.getTitle(),
                                    subTask.getCompleted()
                            );
                        })
                        .toList(),
                boardTask.getComments().stream()
                        .map(comment -> {
                            return new CommentResponse(
                                    comment.getId(),
                                    new BoardMemberResponse(
                                            comment.getUser().getId(),
                                            comment.getUser().getName(),
                                            comment.getUser().getEmail(),
                                            comment.getUser().getUrlAvatar(),
                                            boardMemberRepository.findByBoardIdAndUserId(boardTask.getBoard().getId(), comment.getUser().getId()).orElseThrow(() -> new EntityNotFoundException("Board member not found.")).getRole()
                                    ),
                                    comment.getTask().getId(),
                                    comment.getContent(),
                                    comment.getCreatedAt()
                            );
                        })
                        .toList()
        );
    }
}
