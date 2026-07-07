package com.taskapp.repository;

import com.taskapp.entity.BoardTask;
import com.taskapp.entity.enums.BoardTaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardTaskRepository extends JpaRepository<BoardTask, UUID> {
    List<BoardTask> findByBoardId(UUID boardId);
    List<BoardTask> findByAssigneeId(UUID assigneeId);
    List<BoardTask> findByBoardIdAndState(UUID boardId, BoardTaskState state);
}
