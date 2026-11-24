package com.kata.bookstore.service;

import com.kata.bookstore.dto.BookResponse;

import java.util.List;

public interface BookService {
    List<BookResponse> getAllBooks();
}
