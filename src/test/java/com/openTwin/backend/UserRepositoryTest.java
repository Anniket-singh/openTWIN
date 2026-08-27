package com.opentwin.backend;

import com.opentwin.backend.entity.User;
import com.opentwin.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {

        String email = "test-" + UUID.randomUUID() + "@opentwin.com";

        User user = new User(
                "Test User",
                email,
                "test-password"
        );

        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());

        User foundUser = userRepository
                .findById(savedUser.getId())
                .orElseThrow();

        assertEquals("Test User", foundUser.getName());
        assertEquals(email, foundUser.getEmail());
    }
}