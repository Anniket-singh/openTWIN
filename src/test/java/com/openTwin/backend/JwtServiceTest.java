package com.opentwin.backend;

import com.opentwin.backend.entity.User;
import com.opentwin.backend.service.JwtService;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    @Test
    void shouldValidateGeneratedToken() throws Exception {

        JwtService jwtService = new JwtService(
                "secretekey2026dxcvhjklpoiufdfbnkiufdcdfghgfdxcvbndsdfsdfxvbnklihghokjhgh",
                900000
        );

        User user = new User(
                "Test User",
                "test@example.com",
                "test-password"
        );

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, 1L);

        String token = jwtService.generateToken(user);

        boolean valid = jwtService.isTokenValid(token);

        assertTrue(valid);
    }
}