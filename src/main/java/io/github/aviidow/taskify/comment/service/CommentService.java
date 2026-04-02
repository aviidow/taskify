package io.github.aviidow.taskify.comment.service;

import io.github.aviidow.taskify.comment.dto.CommentRequestDto;
import io.github.aviidow.taskify.comment.dto.CommentResponseDto;
import io.github.aviidow.taskify.comment.mapper.CommentMapper;
import io.github.aviidow.taskify.comment.model.Comment;
import io.github.aviidow.taskify.comment.repository.CommentRepository;
import io.github.aviidow.taskify.task.model.Task;
import io.github.aviidow.taskify.task.repository.TaskRepository;
import io.github.aviidow.taskify.user.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentResponseDto createComment(Long taskId, CommentRequestDto dto, User author) {
        log.info("Creating comment for task {} by user {}", taskId, author.getEmail());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        checkTaskAccess(task, author);

        Comment comment = commentMapper.toEntity(dto, author, task);
        Comment savedComment = commentRepository.save(comment);

        log.info("Comment created successfully with id: {}", savedComment.getId());
        return commentMapper.toResponseDto(savedComment, author.getId(), isAdmin(author));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getCommentsByTaskId(Long taskId, User currentUser, Pageable pageable) {
        log.info("Getting comments for task {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found with id: " + taskId));

        checkTaskAccess(task, currentUser);

        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId, pageable)
                .map(comment -> commentMapper.toResponseDto(comment, currentUser.getId(), isAdmin(currentUser)));
    }

    @Transactional(readOnly = true)
    public CommentResponseDto getCommentById(Long id, User currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        checkTaskAccess(comment.getTask(), currentUser);

        return commentMapper.toResponseDto(comment, currentUser.getId(), isAdmin(currentUser));
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto dto, User currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new RuntimeException("You don't have permission to edit this comment");
        }

        commentMapper.updateEntity(comment, dto);
        Comment updatedComment = commentRepository.save(comment);

        log.info("Comment updated successfully with id: {}", id);
        return commentMapper.toResponseDto(updatedComment, currentUser.getId(), isAdmin(currentUser));
    }

    @Transactional
    public void deleteComment(Long id, User currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        if (!comment.getAuthor().getId().equals(currentUser.getId()) && !isAdmin(currentUser)) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
        log.info("Comment deleted successfully with id: {}", id);
    }

    private void checkTaskAccess(Task task, User user) {
        boolean hasAccess = task.getCreator().getId().equals(user.getId()) ||
                (task.getAssignee() != null && task.getAssignee().getId().equals(user.getId())) ||
                isAdmin(user);

        if (!hasAccess) {
            throw new RuntimeException("You don't have permission to access comments for this task");
        }
    }

    private boolean isAdmin(User user) {
        return user.getRole() == User.Role.ADMIN;
    }
}
