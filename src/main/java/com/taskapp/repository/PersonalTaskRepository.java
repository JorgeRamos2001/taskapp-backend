package com.taskapp.repository;

import com.taskapp.entity.PersonalTask;
import com.taskapp.entity.enums.PersonalTaskState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PersonalTaskRepository extends JpaRepository<PersonalTask, UUID> {
    List<PersonalTask> findByOwnerId(UUID userId);
    List<PersonalTask> findByOwnerIdAndState(UUID userId, PersonalTaskState state);
}
