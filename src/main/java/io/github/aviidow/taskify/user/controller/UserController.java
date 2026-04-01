package io.github.aviidow.taskify.user.controller;

import io.github.aviidow.taskify.user.dto.UserRegistrationDto;
import io.github.aviidow.taskify.user.dto.UserResponseDto;
import io.github.aviidow.taskify.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        log.info("Received registration request for email: {}", registrationDto.getEmail());
        UserResponseDto response = userService.register(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/id")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable Long id) {
        // TODO: реализовать после добавления безопасности
        return ResponseEntity.notFound().build();
    }
}
