package io.github.aviidow.taskify.auth.controller;

import io.github.aviidow.taskify.auth.dto.AuthRequestDto;
import io.github.aviidow.taskify.auth.dto.AuthResponseDto;
import io.github.aviidow.taskify.security.JwtService;
import io.github.aviidow.taskify.user.model.User;
import io.github.aviidow.taskify.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto request) {
        log.info("Login attempt for email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();
        String token = jwtService.generateToken(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponseDto.builder()
                .token(token)
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
