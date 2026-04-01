package io.github.aviidow.taskify.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String email;
    private String name;
    private LocalDateTime createdAt;
}
