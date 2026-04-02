package io.github.aviidow.taskify.comment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentRequestDto {

    @NotBlank(message = "Comment content is required")
    private String content;
}
