package com.kata.bookstore.service.impl;

import com.kata.bookstore.dto.AuthResponse;
import com.kata.bookstore.dto.RegisterRequest;
import com.kata.bookstore.entity.User;
import com.kata.bookstore.entity.UserRole;
import com.kata.bookstore.entity.UserStatus;
import com.kata.bookstore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @InjectMocks
    private DatabaseUserDetailsService userDetailsService;

    @Test
    void shouldRegisterUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Shipra");
        request.setEmail("shipra@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("Registration Successful", response.getMessage());
        assertEquals("shipra@gmail.com", response.getEmail());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).saveAndFlush(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("Shipra", savedUser.getName());
        assertEquals("shipra@gmail.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(UserRole.USER, savedUser.getRole());
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());
    }

    @Test
    void shouldThrowConflictExceptionWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Shipra");
        request.setEmail("shipra@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Email is already in use", exception.getReason());
    }

    @Test
    void shouldEncodePasswordBeforeSavingUser() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Shipra");
        request.setEmail("shipra@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        authService.register(request);

        InOrder inOrder = inOrder(passwordEncoder, userRepository);
        inOrder.verify(passwordEncoder).encode("password123");
        inOrder.verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    void shouldNotSaveUserWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Shipra");
        request.setEmail("shipra@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        verify(userRepository, never()).saveAndFlush(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldThrowConflictWhenDuplicateEmailWinsTheRegistrationRace() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Shipra");
        request.setEmail("shipra@gmail.com");
        request.setPassword("password123");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate email"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Email is already in use", exception.getReason());
    }

    @Test
    void shouldLoadUserSuccessfullyByEmail() {
        User user = User.createRegistered("Shipra", "shipra@gmail.com", "$2a$10$encodedPassword");

        when(userRepository.findByEmail("shipra@gmail.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("shipra@gmail.com");

        assertNotNull(userDetails);
        assertEquals("shipra@gmail.com", userDetails.getUsername());
        assertEquals("$2a$10$encodedPassword", userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));

        verify(userRepository, times(1))
                .findByEmail("shipra@gmail.com");
    }

    @Test
    void shouldLoadRoleAndDisabledStatusFromUser() {
        User user = User.builder()
                .name("Admin")
                .email("admin@example.com")
                .password("encodedPassword")
                .role(UserRole.ADMIN)
                .status(UserStatus.DISABLED)
                .build();
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        assertFalse(userDetails.isEnabled());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findByEmail("unknown@gmail.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown@gmail.com"));
    }
}
