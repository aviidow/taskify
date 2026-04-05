package io.github.aviidow.taskify.task.service;

import io.github.aviidow.taskify.exception.ResourceNotFoundException;
import io.github.aviidow.taskify.notification.EmailService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final EmailService emailService;

    @Transactional
    public TaskResponseDto createTask(TaskRequestDto dto, User creator) {
        log.info("Creating task for user: {}", creator.getEmail());

        User assignee = null;
        if (dto.getAssigneeId() != null) {
            assignee = userRepository.findById(dto.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("User(assignee)", "id", dto.getAssigneeId()));
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
    public TaskResponseDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        return taskMapper.toResponseDto(task);
    }

    @Transactional
    public TaskResponseDto updateTask(Long id, TaskRequestDto dto) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        User assignee = dto.getAssigneeId() != null
                ? userRepository.findById(dto.getAssigneeId()).orElse(null)
                : null;

        taskMapper.updateEntity(task, dto, assignee);
        Task updatedTask = taskRepository.save(task);

        log.info("Task updated successfully with id: {}", id);
        return taskMapper.toResponseDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));
        taskRepository.delete(task);
        log.info("Task deleted successfully with id: {}", id);
    }

    @Transactional
    public TaskResponseDto assignTask(Long id, Long assigneeId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", id));

        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", assigneeId));

        task.setAssignee(assignee);
        Task updatedTask = taskRepository.save(task);

        emailService.sendTaskAssignmentEmail(
                assignee.getEmail(),
                task.getTitle(),
                assignee.getName()
        );

        log.info("Task {} assigned to user {}", id, assignee.getEmail());
        return taskMapper.toResponseDto(updatedTask);
    }
}
