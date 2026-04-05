package io.github.aviidow.taskify.security;

import io.github.aviidow.taskify.comment.model.Comment;
import io.github.aviidow.taskify.comment.repository.CommentRepository;
import io.github.aviidow.taskify.task.model.Task;
import io.github.aviidow.taskify.task.repository.TaskRepository;
import io.github.aviidow.taskify.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("commentSecurity")
@RequiredArgsConstructor
@Slf4j
public class CommentSecurity {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;

    public boolean canCreateComment(Long taskId) {
        User currentUser = getCurrentUser();
        Task task = taskRepository.findById(taskId).orElse(null);

        if (task == null) {
            log.warn("Task not found for comment creation: {}", taskId);
            return false;
        }

        if (currentUser.getRole() == User.Role.ADMIN) return true;

        return task.getCreator().getId().equals(currentUser.getId()) ||
                (task.getAssignee() != null && task.getAssignee().getId().equals(currentUser.getId()));
    }

    public boolean canViewComments(Long taskId) {
        return canCreateComment(taskId);
    }

    public boolean canEditComment(Long commentId) {
        User currentUser = getCurrentUser();
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            log.warn("Comment not found for edit: {}", commentId);
            return false;
        }

        if (currentUser.getRole() == User.Role.ADMIN) return true;

        boolean isAuthor = comment.getAuthor().getId().equals(currentUser.getId());

        if (isAuthor) {
            boolean isWithinEditWindow = comment.getCreatedAt()
                    .isAfter(LocalDateTime.now().minusMinutes(5));
            if (!isWithinEditWindow) {
                log.warn("Edit window expired for comment: {}", commentId);
                return false;
            }
        }

        return isAuthor;
    }

    public boolean canDeleteComment(Long commentId) {
        User currentUser = getCurrentUser();
        Comment comment = commentRepository.findById(commentId).orElse(null);

        if (comment == null) {
            log.warn("Comment not found for delete: {}", commentId);
            return false;
        }

        if (currentUser.getRole() == User.Role.ADMIN) return true;

        return comment.getAuthor().getId().equals(currentUser.getId());
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }
}
