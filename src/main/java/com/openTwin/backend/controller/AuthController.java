package com.opentwin.backend.controller;

import com.opentwin.backend.dto.LoginRequest;
import com.opentwin.backend.dto.LoginResponse;
import com.opentwin.backend.dto.RegisterRequest;
import com.opentwin.backend.dto.RegisterResponse;
import com.opentwin.backend.entity.User;
import com.opentwin.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.opentwin.backend.dto.UserResponse;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        User user = authService.register(request);

        RegisterResponse response = new RegisterResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        String token = authService.login(request);

        return ResponseEntity.ok(
                new LoginResponse(token)
        );
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(
            Authentication authentication
    ) {

        String userId = authentication.getPrincipal().toString();

        User user = authService.getUserById(
                Long.parseLong(userId)
        );

        UserResponse response = new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail()
        );

        return ResponseEntity.ok(response);
    }
}