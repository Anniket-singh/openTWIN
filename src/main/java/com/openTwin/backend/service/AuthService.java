package com.opentwin.backend.service;

import com.opentwin.backend.dto.RegisterRequest;
import com.opentwin.backend.entity.User;
import com.opentwin.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.opentwin.backend.exception.EmailAlreadyRegisteredException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyRegisteredException(
                    "Email is already registered"
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                encodedPassword
        );

        return userRepository.save(user);
    }
}