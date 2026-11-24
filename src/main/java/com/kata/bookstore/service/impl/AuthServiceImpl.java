package com.kata.bookstore.service.impl;

import com.kata.bookstore.dto.AuthResponse;
import com.kata.bookstore.dto.LoginRequest;
import com.kata.bookstore.dto.RegisterRequest;
import com.kata.bookstore.entity.User;
import com.kata.bookstore.repository.UserRepository;
import com.kata.bookstore.service.AuthService;
import org.springframework.dao.DataIntegrityViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration rejected because the email is already in use");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.createRegistered(request.getName(), request.getEmail(), encodedPassword);
        try {
            userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            log.warn("Registration rejected because the email is already in use");
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already in use", exception);
        }

        log.info("User registration completed");
        return AuthResponse.builder()
                .message("Registration Successful")
                .email(user.getEmail())
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        log.info("User login completed");
        return AuthResponse.builder()
                .message("Login Successful")
                .email(request.getEmail())
                .build();
    }
}
