package io.github.aviidow.taskify.task.dto;

import io.github.aviidow.taskify.task.model.Task;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TaskResponseDto {
    private Long id;
    private String title;
    private String description;
    private Task.Status status;
    private Task.Priority priority;
    private LocalDateTime deadline;
    private Long creatorId;
    private String creatorEmail;
    private Long assigneeId;
    private String assigneeEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
