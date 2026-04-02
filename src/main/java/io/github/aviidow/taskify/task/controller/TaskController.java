package io.github.aviidow.taskify.task.controller;

import io.github.aviidow.taskify.task.dto.TaskRequestDto;
import io.github.aviidow.taskify.task.dto.TaskResponseDto;
import io.github.aviidow.taskify.task.service.TaskService;
import io.github.aviidow.taskify.user.model.User;
import io.github.aviidow.taskify.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<TaskResponseDto> createTask(
            @Valid @RequestBody TaskRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        TaskResponseDto createdTask = taskService.createTask(dto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTask);
    }

    @GetMapping
    public ResponseEntity<Page<TaskResponseDto>> getMyTasks(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        Page<TaskResponseDto> tasks = taskService.getTasksForUser(currentUser, pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDto> getTaskById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        TaskResponseDto task = taskService.getTaskById(id, currentUser);
        return ResponseEntity.ok(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDto> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        TaskResponseDto updatedTask = taskService.updateTask(id, dto, currentUser);
        return ResponseEntity.ok(updatedTask);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        taskService.deleteTask(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/assign/{assigneeId}")
    public ResponseEntity<TaskResponseDto> assignTask(
            @PathVariable Long id,
            @PathVariable Long assigneeId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        TaskResponseDto updatedTask = taskService.assignTask(id, assigneeId, currentUser);
        return ResponseEntity.ok(updatedTask);
    }
}
