package com.taskapp.personaltask;

import com.taskapp.personaltask.dto.request.PersonalTaskRequest;
import com.taskapp.personaltask.dto.request.UpdatePersonalTaskRequest;
import com.taskapp.personaltask.dto.response.PersonalTaskResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/personal-tasks")
@RequiredArgsConstructor
class PersonalTaskController {

    private final PersonalTaskService personalTaskService;

    @PostMapping
    ResponseEntity<PersonalTaskResponse> createPersonalTask(@RequestBody @Valid PersonalTaskRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(personalTaskService.create(request, authentication.getName()));
    }

    @GetMapping
    ResponseEntity<List<PersonalTaskResponse>> getPersonalTasks(Authentication authentication) {
        return ResponseEntity.ok(personalTaskService.getPersonalTasks(authentication.getName()));
    }

    @GetMapping("/{id}")
    ResponseEntity<PersonalTaskResponse> getPersonalTask(@PathVariable UUID id, Authentication authentication) {
        return ResponseEntity.ok(personalTaskService.getPersonalTask(authentication.getName(), id));
    }

    @PutMapping("/{id}")
    ResponseEntity<PersonalTaskResponse> updatePersonalTask(@PathVariable UUID id, @RequestBody @Valid UpdatePersonalTaskRequest request, Authentication authentication) {
        return ResponseEntity.ok(personalTaskService.update(id, request, authentication.getName()));
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deletePersonalTask(@PathVariable UUID id, Authentication authentication) {
        personalTaskService.delete(authentication.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
