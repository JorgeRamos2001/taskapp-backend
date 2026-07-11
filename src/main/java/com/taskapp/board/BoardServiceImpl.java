package com.taskapp.board;

import com.taskapp.auth.entity.User;
import com.taskapp.auth.repository.UserRepository;
import com.taskapp.board.dto.request.AddBoardMember;
import com.taskapp.board.dto.request.BoardRequest;
import com.taskapp.board.dto.request.RemoveBoardMember;
import com.taskapp.board.dto.response.BoardMemberResponse;
import com.taskapp.board.dto.response.BoardResponse;
import com.taskapp.board.entity.Board;
import com.taskapp.board.entity.BoardMember;
import com.taskapp.board.entity.enums.BoardMemberRole;
import com.taskapp.board.repository.BoardMemberRepository;
import com.taskapp.board.repository.BoardRepository;
import com.taskapp.shared.exception.EntityNotFoundException;
import com.taskapp.shared.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardServiceImpl implements BoardService {
    private final BoardRepository boardRepository;
    private final BoardMemberRepository boardMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BoardResponse createBoard(BoardRequest request, String email) {
        log.warn("Creating board for user: {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        Board board = Board.builder()
                .owner(user)
                .title(request.title())
                .description(request.description())
                .build();

        Board savedBoard = boardRepository.save(board);

        BoardMember boardMember = BoardMember.builder()
                .board(savedBoard)
                .user(user)
                .role(BoardMemberRole.ADMIN)
                .build();
        boardMemberRepository.save(boardMember);
        return convertToResponse(savedBoard);
    }

    @Override
    @Transactional(readOnly = true)
    public BoardResponse getBoard(String email, UUID boardId) {
        log.warn("Getting board for user: {}", email);

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!user.getId().equals(board.getOwner().getId()) && board.getBoardMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new ValidationException("User does not have access to this board.");
        }

        return convertToResponse(board);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponse> getMyBoards(String email) {
        log.warn("Getting my boards for user: {}", email);

        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        return boardRepository.findByOwnerId(user.getId()).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponse> getAllBoards(String email) {
        log.warn("Getting all boards for user: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        return boardRepository.findAllAccessibleByUser(user.getId()).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional
    public BoardResponse updateBoard(UUID boardId, BoardRequest request, String email) {
        log.warn("Updating board for user: {}", email);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!user.getId().equals(board.getOwner().getId())) {
            throw new ValidationException("User does not have access to this board.");
        }
        board.setTitle(request.title());
        board.setDescription(request.description());
        Board savedBoard = boardRepository.save(board);
        return convertToResponse(savedBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(String email, UUID boardId) {
        log.warn("Deleting board for user: {}", email);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        if (!user.getId().equals(board.getOwner().getId())) {
            throw new ValidationException("User does not have access to this board.");
        }
        boardRepository.delete(board);
    }

    @Override
    @Transactional
    public void addMember(UUID boardId, AddBoardMember request, String email) {
        log.warn("Adding member to board for user: {}", email);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        User addMember = userRepository.findById(request.userId()).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new ValidationException("User does not have access to this board.");
        }

        if (user.getId().equals(addMember.getId())) {
            throw new ValidationException("User cannot add themselves to the board.");
        }

        if (boardMemberRepository.existsByBoardIdAndUserId(boardId, addMember.getId())) {
            throw new ValidationException("User is already a member of this board.");
        }

        BoardMember boardMember = BoardMember.builder()
                .board(board)
                .user(addMember)
                .role(request.role())
                .build();
        boardMemberRepository.save(boardMember);
    }

    @Override
    @Transactional
    public void removeMember(UUID boardId, RemoveBoardMember request, String email) {
        log.warn("Removing member from board for user: {}", email);
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new EntityNotFoundException("Board not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        User removeMember = userRepository.findById(request.userId()).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!board.getOwner().getId().equals(user.getId())) {
            throw new ValidationException("User does not have access to this board.");
        }

        if (!boardMemberRepository.existsByBoardIdAndUserId(boardId, removeMember.getId())) {
            throw new ValidationException("User is not a member of this board.");
        }

        BoardMember boardMember = boardMemberRepository.findByBoardIdAndUserId(boardId, removeMember.getId()).orElseThrow(() -> new EntityNotFoundException("Board member not found."));
        boardMemberRepository.delete(boardMember);
    }

    private BoardResponse convertToResponse(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getOwner().getName(),
                board.getTitle(),
                board.getDescription(),
                board.getCreatedAt(),
                board.getBoardMembers().stream()
                        .map(this::convertToResponse)
                        .toList()
        );
    }

    private BoardMemberResponse convertToResponse(BoardMember boardMember) {
        return new BoardMemberResponse(
                boardMember.getId(),
                boardMember.getUser().getName(),
                boardMember.getUser().getEmail(),
                boardMember.getUser().getUrlAvatar(),
                boardMember.getRole()
        );
    }
}
