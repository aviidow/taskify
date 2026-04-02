package io.github.aviidow.taskify.task.service;

import io.github.aviidow.taskify.task.dto.TaskRequestDto;
import io.github.aviidow.taskify.task.dto.TaskResponseDto;
import io.github.aviidow.taskify.task.mapper.TaskMapper;
import io.github.aviidow.taskify.task.model.Task;
import io.github.aviidow.taskify.task.repository.TaskRepository;
import io.github.aviidow.taskify.user.model.User;
import io.github.aviidow.taskify.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Transactional
    public TaskResponseDto createTask(TaskRequestDto dto, User creator) {
        log.info("Creating task for user: {}", creator.getEmail());

        User assignee = null;
        if (dto.getAssigneeId() != null) {
            assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found with id: " + dto.getAssigneeId()));
        }

        Task task = taskMapper.toEntity(dto, creator, assignee);
        Task savedTask = taskRepository.save(task);

        log.info("Task created successfully with id: {}", savedTask.getId());
        return taskMapper.toResponseDto(savedTask);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponseDto> getTasksForUser(User user, Pageable pageable) {
        log.info("Getting tasks for user: {}", user.getEmail());
        return taskRepository.findAllByUserId(user.getId(), pageable)
                .map(taskMapper::toResponseDto);
    }

    @Transactional(readOnly = true)
    public TaskResponseDto getTaskById(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        if (!task.getCreator().getId().equals(currentUser.getId()) &&
                (task.getAssignee() == null || !task.getAssignee().getId().equals(currentUser.getId())) &&
                !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You don't have permission to view this task");
        }

        return taskMapper.toResponseDto(task);
    }

    @Transactional
    public TaskResponseDto updateTask(Long id, TaskRequestDto dto, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        if (!task.getCreator().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You don't have permission to update this task");
        }

        User assignee = null;
        if (dto.getAssigneeId() != null) {
            assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new RuntimeException("Assignee not found with id: " + dto.getAssigneeId()));
        }

        taskMapper.updateEntity(task, dto, assignee);
        Task updatedTask = taskRepository.save(task);

        log.info("Task updated successfully with id: {}", id);
        return taskMapper.toResponseDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        if (!task.getCreator().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You don't have permission to delete this task");
        }

        taskRepository.delete(task);
        log.info("Task deleted successfully with id: {}", id);
    }

    @Transactional
    public TaskResponseDto assignTask(Long id, Long assigneeId, User currentUser) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + id));

        if (!task.getCreator().getId().equals(currentUser.getId()) &&
                !currentUser.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You don't have permission to assign this task");
        }

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + assigneeId));

        task.setAssignee(assignee);
        Task updatedTask = taskRepository.save(task);

        log.info("Task {} assigned to user {}", id, assignee.getEmail());
        return taskMapper.toResponseDto(updatedTask);
    }
}
