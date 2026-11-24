package com.kata.bookstore.service;

import com.kata.bookstore.dto.BookResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    Page<BookResponse> getAllBooks(Pageable pageable);
}
