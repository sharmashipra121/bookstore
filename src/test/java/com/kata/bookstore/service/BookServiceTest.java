package com.kata.bookstore.service;

import com.kata.bookstore.entity.Book;
import com.kata.bookstore.repository.BookRepository;
import com.kata.bookstore.service.impl.BookServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    void shouldReturnAllBooks() {

        List<Book> books = Arrays.asList(
                new Book(1L, "Effective Java",
                        "Joshua Bloch", BigDecimal.valueOf(650), 10),

                new Book(2L, "Clean Code",
                        "Robert Martin", BigDecimal.valueOf(700), 5)
        );

        when(bookRepository.findAll())
                .thenReturn(books);

        List<Book> result = bookService.getAllBooks();

        assertEquals(2, result.size());
        assertEquals("Effective Java", result.get(0).getTitle());

        verify(bookRepository, times(1)).findAll();
    }
}
