package io.github.aviidow.taskify.comment.controller;

import io.github.aviidow.taskify.comment.dto.CommentRequestDto;
import io.github.aviidow.taskify.comment.dto.CommentResponseDto;
import io.github.aviidow.taskify.comment.service.CommentService;
import io.github.aviidow.taskify.user.model.User;
import io.github.aviidow.taskify.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;

    @PostMapping("/tasks/{taskId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        CommentResponseDto comment = commentService.createComment(taskId, dto, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(comment);
    }

    @GetMapping("/tasks/{taskId}/comments")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByTask(
            @PathVariable Long taskId,
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        Page<CommentResponseDto> comments = commentService.getCommentsByTaskId(taskId, currentUser, pageable);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDto> getCommentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        CommentResponseDto comment = commentService.getCommentById(id, currentUser);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequestDto dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        CommentResponseDto updatedComment = commentService.updateComment(id, dto, currentUser);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User currentUser = userService.findByEmail(userDetails.getUsername());
        commentService.deleteComment(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
