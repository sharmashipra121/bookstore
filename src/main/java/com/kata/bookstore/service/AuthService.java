package com.kata.bookstore.service;

import com.kata.bookstore.dto.AuthResponse;
import com.kata.bookstore.dto.RegisterRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
}
