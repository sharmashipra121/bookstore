package com.kata.bookstore.service.impl;

import com.kata.bookstore.dto.BookResponse;
import com.kata.bookstore.entity.Book;
import com.kata.bookstore.repository.BookRepository;
import com.kata.bookstore.service.BookService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::toBookResponse)
                .toList();
    }

    private BookResponse toBookResponse(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getPrice(),
                book.getStock()
        );
    }
}
