package com.taskapp.service.implementations;

import com.taskapp.dto.request.PersonalTaskRequest;
import com.taskapp.dto.request.UpdatePersonalTaskRequest;
import com.taskapp.dto.response.PersonalTaskResponse;
import com.taskapp.entity.PersonalTask;
import com.taskapp.entity.User;
import com.taskapp.entity.enums.PersonalTaskState;
import com.taskapp.exception.EntityNotFoundException;
import com.taskapp.exception.ValidationException;
import com.taskapp.repository.PersonalTaskRepository;
import com.taskapp.repository.UserRepository;
import com.taskapp.service.PersonalTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalTaskServiceImp implements PersonalTaskService {
    private final PersonalTaskRepository personalTaskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PersonalTaskResponse create(PersonalTaskRequest request, String email) {
        log.warn("Creating personal task for user: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        PersonalTask personalTask = PersonalTask.builder()
                .owner(user)
                .title(request.title())
                .description(request.description())
                .priority(request.priority())
                .state(PersonalTaskState.IN_PROGRESS)
                .dueDate(request.dueDate())
                .build();

        PersonalTask savedTask = personalTaskRepository.save(personalTask);
        return convertToResponse(savedTask);
    }

    @Override
    @Transactional(readOnly = true)
    public PersonalTaskResponse getPersonalTask(String email, UUID taskId) {
        log.warn("Getting personal task for user: {}", email);
        PersonalTask personalTask = personalTaskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Personal task not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!user.getId().equals(personalTask.getOwner().getId())) {
            throw new ValidationException("User does not own this personal task.");
        }
        return convertToResponse(personalTask);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonalTaskResponse> getPersonalTasks(String email) {
        log.warn("Getting personal tasks for user: {}", email);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));
        return personalTaskRepository.findByOwnerId(user.getId()).stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    @Transactional
    public PersonalTaskResponse update(UUID personalTaskId, UpdatePersonalTaskRequest request, String email) {
        log.warn("Updating personal task for user: {}", email);
        PersonalTask personalTask = personalTaskRepository.findById(personalTaskId).orElseThrow(() -> new EntityNotFoundException("Personal task not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!user.getId().equals(personalTask.getOwner().getId())) {
            throw new ValidationException("User does not own this personal task.");
        }

        personalTask.setTitle(request.title());
        personalTask.setDescription(request.description());
        personalTask.setPriority(request.priority());
        personalTask.setState(request.state());
        personalTask.setDueDate(request.dueDate());

        PersonalTask savedTask = personalTaskRepository.save(personalTask);
        return convertToResponse(savedTask);
    }

    @Override
    @Transactional
    public void delete(String email, UUID taskId) {
        log.warn("Deleting personal task for user: {}", email);
        PersonalTask personalTask = personalTaskRepository.findById(taskId).orElseThrow(() -> new EntityNotFoundException("Personal task not found."));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("User not found."));

        if (!user.getId().equals(personalTask.getOwner().getId())) {
            throw new ValidationException("User does not own this personal task.");
        }
        personalTaskRepository.delete(personalTask);
    }

    private PersonalTaskResponse convertToResponse(PersonalTask personalTask) {
        return new PersonalTaskResponse(
                personalTask.getId(),
                personalTask.getOwner().getId(),
                personalTask.getTitle(),
                personalTask.getDescription(),
                personalTask.getPriority(),
                personalTask.getState(),
                personalTask.getDueDate(),
                personalTask.getCreatedAt()
        );
    }
}
