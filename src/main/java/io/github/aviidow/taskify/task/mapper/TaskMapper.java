package io.github.aviidow.taskify.task.mapper;

import io.github.aviidow.taskify.task.dto.TaskRequestDto;
import io.github.aviidow.taskify.task.dto.TaskResponseDto;
import io.github.aviidow.taskify.task.model.Task;
import io.github.aviidow.taskify.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toEntity(TaskRequestDto dto, User creator, User assignee) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDeadline(dto.getDeadline());
        task.setCreator(creator);
        task.setAssignee(assignee);
        return task;
    }

    public TaskResponseDto toResponseDto(Task task) {
        return TaskResponseDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .deadline(task.getDeadline())
                .creatorId(task.getCreator().getId())
                .creatorEmail(task.getCreator().getEmail())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .assigneeEmail(task.getAssignee() != null ? task.getAssignee().getEmail() : null)
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }

    public void updateEntity(Task task, TaskRequestDto dto, User assignee) {
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setStatus(dto.getStatus());
        task.setPriority(dto.getPriority());
        task.setDeadline(dto.getDeadline());
        task.setAssignee(assignee);
    }
}
