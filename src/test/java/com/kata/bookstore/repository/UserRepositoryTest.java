package com.kata.bookstore.repository;

import com.kata.bookstore.entity.User;
import com.kata.bookstore.entity.UserRole;
import com.kata.bookstore.entity.UserStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUserSuccessfully() {
        User user = User.createRegistered("Shipra", "shipra@gmail.com", "encodedPassword");
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("Shipra", savedUser.getName());
        assertEquals("shipra@gmail.com", savedUser.getEmail());
        assertEquals(UserRole.USER, savedUser.getRole());
        assertEquals(UserStatus.ACTIVE, savedUser.getStatus());
    }

    @Test
    void shouldFindUserByEmail() {
        userRepository.save(User.createRegistered("Shipra", "shipra@gmail.com", "encodedPassword"));
        Optional<User> result = userRepository.findByEmail("shipra@gmail.com");

        assertTrue(result.isPresent());
        assertEquals("Shipra", result.get().getName());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        userRepository.save(User.createRegistered("Shipra", "shipra@gmail.com", "encodedPassword"));
        boolean exists = userRepository.existsByEmail("shipra@gmail.com");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("unknown@gmail.com");
        assertFalse(exists);
    }
}
