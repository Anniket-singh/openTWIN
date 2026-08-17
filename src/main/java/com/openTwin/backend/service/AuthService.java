package com.opentwin.backend.service;

import com.opentwin.backend.dto.LoginRequest;
import com.opentwin.backend.dto.RegisterRequest;
import com.opentwin.backend.entity.User;
import com.opentwin.backend.exception.InvalidCredentialsException;
import com.opentwin.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.opentwin.backend.exception.EmailAlreadyRegisteredException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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

    public User authenticate(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                                new InvalidCredentialsException("Invalid email or password")
                );

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException(
                    "Invalid email or password"
            );
        }

        return user;
    }

    public String login(LoginRequest request) {

        User user = authenticate(request);

        return jwtService.generateToken(user);
    }

    public User getUserById(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );
    }
}