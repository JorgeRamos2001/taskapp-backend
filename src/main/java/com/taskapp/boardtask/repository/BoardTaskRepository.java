package com.taskapp.boardtask.repository;

import com.taskapp.boardtask.entity.BoardTask;
import com.taskapp.boardtask.entity.enums.BoardTaskState;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BoardTaskRepository extends JpaRepository<BoardTask, UUID> {

    @EntityGraph(attributePaths = {"assignee", "subTasks", "comments", "comments.user"})
    List<BoardTask> findByBoardId(UUID boardId);

    @EntityGraph(attributePaths = {"assignee", "subTasks", "comments", "comments.user"})
    List<BoardTask> findByAssigneeId(UUID assigneeId);

    @EntityGraph(attributePaths = {"assignee", "subTasks", "comments", "comments.user"})
    List<BoardTask> findByBoardIdAndState(UUID boardId, BoardTaskState state);
}
