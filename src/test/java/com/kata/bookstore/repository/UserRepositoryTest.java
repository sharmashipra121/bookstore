package com.kata.bookstore.repository;

import com.kata.bookstore.entity.User;
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
        User user = new User();
        user.setName("Shipra");
        user.setEmail("shipra@gmail.com");
        user.setPassword("encodedPassword");
        User savedUser = userRepository.save(user);

        assertNotNull(savedUser.getId());
        assertEquals("Shipra", savedUser.getName());
        assertEquals("shipra@gmail.com", savedUser.getEmail());
    }

    @Test
    void shouldFindUserByEmail() {
        User user = new User();
        user.setName("Shipra");
        user.setEmail("shipra@gmail.com");
        user.setPassword("encodedPassword");

        userRepository.save(user);
        Optional<User> result = userRepository.findByEmail("shipra@gmail.com");

        assertTrue(result.isPresent());
        assertEquals("Shipra", result.get().getName());
    }

    @Test
    void shouldReturnTrueWhenEmailExists() {
        User user = new User();
        user.setName("Shipra");
        user.setEmail("shipra@gmail.com");
        user.setPassword("encodedPassword");

        userRepository.save(user);
        boolean exists = userRepository.existsByEmail("shipra@gmail.com");

        assertTrue(exists);
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() {
        boolean exists = userRepository.existsByEmail("unknown@gmail.com");
        assertFalse(exists);
    }
}
