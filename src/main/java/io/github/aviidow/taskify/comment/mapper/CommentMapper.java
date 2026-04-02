package io.github.aviidow.taskify.comment.mapper;

import io.github.aviidow.taskify.comment.dto.CommentRequestDto;
import io.github.aviidow.taskify.comment.dto.CommentResponseDto;
import io.github.aviidow.taskify.comment.model.Comment;
import io.github.aviidow.taskify.task.model.Task;
import io.github.aviidow.taskify.user.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestDto dto, User author, Task task) {
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setAuthor(author);
        comment.setTask(task);
        return comment;
    }

    public CommentResponseDto toResponseDto(Comment comment, Long currentUserId, boolean isAdmin) {
        boolean isAuthor = comment.getAuthor().getId().equals(currentUserId);

        boolean canEdit = isAuthor &&
                comment.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(5));

        boolean canDelete = isAuthor || isAdmin;

        return CommentResponseDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorId(comment.getAuthor().getId())
                .authorEmail(comment.getAuthor().getEmail())
                .authorName(comment.getAuthor().getName())
                .taskId(comment.getTask().getId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .canEdit(canEdit)
                .canDelete(canDelete)
                .build();
    }

    public void updateEntity(Comment comment, CommentRequestDto dto) {
        comment.setContent(dto.getContent());
    }
}
