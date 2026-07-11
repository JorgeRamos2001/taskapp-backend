package com.taskapp.board.repository;

import com.taskapp.board.entity.BoardMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BoardMemberRepository extends JpaRepository<BoardMember, UUID> {
    Optional<BoardMember> findByBoardIdAndUserId(UUID boardId, UUID userId);
    boolean existsByBoardIdAndUserId(UUID boardId, UUID userId);
    List<BoardMember> findByBoardId(UUID boardId);
}
