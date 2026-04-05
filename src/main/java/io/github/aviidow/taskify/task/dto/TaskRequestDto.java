package io.github.aviidow.taskify.task.dto;

import io.github.aviidow.taskify.task.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskRequestDto {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;

    @NotNull(message = "Status is required")
    private Task.Status status = Task.Status.PENDING;

    @NotNull(message = "Priority is required")
    private Task.Priority priority = Task.Priority.MEDIUM;

    private LocalDateTime deadline;

    private Long assigneeId;
}
