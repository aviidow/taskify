package io.github.aviidow.taskify.task.dto;

import io.github.aviidow.taskify.task.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequestDto {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Status is required")
    private Task.Status status = Task.Status.PENDING;

    @NotNull(message = "Priority is required")
    private Task.Priority priority = Task.Priority.MEDIUM;

    private LocalDateTime deadline;

    private Long assigneeId;
}
