package com.taskapp.comment;

import com.taskapp.auth.entity.User;
import com.taskapp.auth.repository.UserRepository;
import com.taskapp.board.dto.response.BoardMemberResponse;
import com.taskapp.board.repository.BoardMemberRepository;
import com.taskapp.boardtask.entity.BoardTask;
import com.taskapp.boardtask.repository.BoardTaskRepository;
import com.taskapp.comment.dto.request.CommentRequest;
import com.taskapp.comment.dto.response.CommentResponse;
import com.taskapp.comment.entity.Comment;
import com.taskapp.comment.repository.CommentRepository;
import com.taskapp.shared.exception.EntityNotFoundException;
import com.taskapp.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final BoardTaskRepository boardTaskRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;

    @Override
    public CommentResponse create(String email, UUID boardTaskId, CommentRequest request) {
        log.warn("Creating comment for user: {}", email);
        BoardTask boardTask = boardTaskRepository.findById(boardTaskId).orElseThrow(() -> new EntityNotFoundException("Board task not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        if (!user.getId().equals(boardTask.getAssignee().getId()) && boardTask.getBoard().getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }
        Comment comment = Comment.builder()
                .task(boardTask)
                .user(user)
                .content(request.content())
                .build();
        Comment savedComment = commentRepository.save(comment);
        return convertToResponse(savedComment);
    }

    @Override
    public void delete(String email, UUID boardTaskId, UUID commentId) {
        log.warn("Deleting comment for user: {}", email);
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        if (!user.getId().equals(comment.getUser().getId())) {
            throw new ValidationException("User does not own this comment.");
        }
        commentRepository.delete(comment);
    }

    private CommentResponse convertToResponse(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                new BoardMemberResponse(
                        comment.getUser().getId(),
                        comment.getUser().getName(),
                        comment.getUser().getEmail(),
                        comment.getUser().getUrlAvatar(),
                        boardMemberRepository.findByBoardIdAndUserId(comment.getTask().getBoard().getId(), comment.getUser().getId()).orElseThrow(() -> new EntityNotFoundException("Board member not found.")).getRole()
                ),
                comment.getTask().getId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}
