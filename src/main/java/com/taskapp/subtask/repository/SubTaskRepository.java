package com.taskapp.subtask.repository;

import com.taskapp.subtask.entity.SubTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubTaskRepository extends JpaRepository<SubTask, UUID> {
    List<SubTask> findByBoardTaskId(UUID boardTaskId);
}
