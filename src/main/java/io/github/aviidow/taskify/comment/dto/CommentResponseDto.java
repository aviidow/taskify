package io.github.aviidow.taskify.comment.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentResponseDto {
    private Long id;
    private String content;
    private Long authorId;
    private String authorEmail;
    private String authorName;
    private Long taskId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean canEdit;
    private boolean canDelete;
}
