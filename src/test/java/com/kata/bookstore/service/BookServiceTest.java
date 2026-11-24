package com.kata.bookstore.service;

import com.kata.bookstore.dto.BookResponse;
import com.kata.bookstore.entity.Book;
import com.kata.bookstore.repository.BookRepository;
import com.kata.bookstore.service.impl.BookServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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

        Book firstBook = new Book();
        firstBook.setId(1L);
        firstBook.setTitle("Effective Java");
        firstBook.setAuthor("Joshua Bloch");
        firstBook.setPrice(BigDecimal.valueOf(650));
        firstBook.setStock(10);

        Book secondBook = new Book();
        secondBook.setId(2L);
        secondBook.setTitle("Clean Code");
        secondBook.setAuthor("Robert Martin");
        secondBook.setPrice(BigDecimal.valueOf(700));
        secondBook.setStock(5);

        List<Book> books = List.of(firstBook, secondBook);
        PageRequest pageable = PageRequest.of(0, 10);

        when(bookRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(books, pageable, books.size()));

        Page<BookResponse> result = bookService.getAllBooks(pageable);

        assertEquals(2, result.getNumberOfElements());
        assertEquals("Effective Java", result.getContent().get(0).getTitle());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals(BigDecimal.valueOf(650), result.getContent().get(0).getPrice());
        assertEquals(10, result.getContent().get(0).getStock());

        verify(bookRepository).findAll(pageable);
    }
}
