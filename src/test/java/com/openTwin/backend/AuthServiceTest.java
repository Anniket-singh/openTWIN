package com.opentwin.backend;

import com.opentwin.backend.dto.RegisterRequest;
import com.opentwin.backend.entity.User;
import com.opentwin.backend.repository.UserRepository;
import com.opentwin.backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUserWithHashedPassword() {

        RegisterRequest request = new RegisterRequest();
        request.setName("Test User");
        request.setEmail("auth-test@opentwin.com");
        request.setPassword("testPassword123");

        User savedUser = authService.register(request);

        assertNotNull(savedUser.getId());

        assertNotEquals(
                "testPassword123",
                savedUser.getPasswordHash()
        );

        assertTrue(
                passwordEncoder.matches(
                        "testPassword123",
                        savedUser.getPasswordHash()
                )
        );

        assertTrue(
                userRepository.existsByEmail("auth-test@opentwin.com")
        );
    }
}