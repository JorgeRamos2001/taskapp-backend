package com.taskapp.board;

import com.taskapp.board.dto.request.AddBoardMember;
import com.taskapp.board.dto.request.BoardRequest;
import com.taskapp.board.dto.request.RemoveBoardMember;
import com.taskapp.board.dto.response.BoardResponse;

import java.util.List;
import java.util.UUID;

public interface BoardService {
    BoardResponse createBoard(BoardRequest request, String email);
    BoardResponse getBoard(String email, UUID boardId);
    List<BoardResponse> getMyBoards(String email);
    List<BoardResponse> getAllBoards(String email);
    BoardResponse updateBoard(UUID boardId, BoardRequest request, String email);
    void deleteBoard(String email, UUID boardId);

    void addMember(UUID boardId, AddBoardMember request, String email);
    void removeMember(UUID boardId, RemoveBoardMember request, String email);
}
