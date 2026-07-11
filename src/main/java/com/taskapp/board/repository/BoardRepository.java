package com.taskapp.board.repository;

import com.taskapp.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardRepository extends JpaRepository<Board, UUID> {
    List<Board> findByOwnerId(UUID owner);

    @Query("""
    SELECT DISTINCT b FROM Board b
    LEFT JOIN b.boardMembers bm
    WHERE b.owner.id = :userId OR bm.user.id = :userId
    """)
    List<Board> findAllAccessibleByUser(@Param("userId") UUID userId);
}
