package com.opentwin.backend;

import com.opentwin.backend.service.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    @Test
    void shouldValidateGeneratedToken() {

        JwtService jwtService = new JwtService(
                "secretekey2026dxcvhjklpoiufdfbnkiufdcdfghgfdxcvbndsdfsdfxvbnklihghokjhgh",
                900000
        );

        // Replace this with the JWT you received from /api/auth/login
        String token = "hello.world.test";

        boolean valid = jwtService.isTokenValid(token);

        assertTrue(valid);
    }
}