package io.github.aviidow.taskify.comment.service;

import io.github.aviidow.taskify.comment.dto.CommentRequestDto;
import io.github.aviidow.taskify.comment.dto.CommentResponseDto;
import io.github.aviidow.taskify.comment.mapper.CommentMapper;
import io.github.aviidow.taskify.comment.model.Comment;
import io.github.aviidow.taskify.comment.repository.CommentRepository;
import io.github.aviidow.taskify.exception.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        Comment comment = commentMapper.toEntity(dto, author, task);
        Comment savedComment = commentRepository.save(comment);

        log.info("Comment created successfully with id: {}", savedComment.getId());
        return commentMapper.toResponseDto(savedComment, author.getId(), isAdmin(author));
    }

    @Transactional(readOnly = true)
    public Page<CommentResponseDto> getCommentsByTaskId(Long taskId, User currentUser, Pageable pageable) {
        log.info("Getting comments for task {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "id", taskId));

        return commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId, pageable)
                .map(comment -> commentMapper.toResponseDto(comment, currentUser.getId(), isAdmin(currentUser)));
    }

    @Transactional(readOnly = true)
    public CommentResponseDto getCommentById(Long id, User currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        return commentMapper.toResponseDto(comment, currentUser.getId(), isAdmin(currentUser));
    }

    @Transactional
    public CommentResponseDto updateComment(Long id, CommentRequestDto dto, User currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        commentMapper.updateEntity(comment, dto);
        Comment updatedComment = commentRepository.save(comment);

        log.info("Comment updated successfully with id: {}", id);
        return commentMapper.toResponseDto(updatedComment, currentUser.getId(), isAdmin(currentUser));
    }

    @Transactional
    public void deleteComment(Long id, User currentUser) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        commentRepository.delete(comment);
        log.info("Comment deleted successfully with id: {}", id);
    }

    private boolean isAdmin(User user) {
        return user.getRole() == User.Role.ADMIN;
    }
}
