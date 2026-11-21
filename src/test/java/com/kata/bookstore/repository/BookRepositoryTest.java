package com.kata.bookstore.repository;

import com.kata.bookstore.entity.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = "spring.sql.init.mode=never")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Test
    void shouldRetrieveAllBooks() {

        Book book1 = new Book();
        book1.setTitle("Effective Java");
        book1.setAuthor("Joshua Bloch");
        book1.setPrice(BigDecimal.valueOf(650));
        book1.setStock(10);

        Book book2 = new Book();
        book2.setTitle("Clean Code");
        book2.setAuthor("Robert Martin");
        book2.setPrice(BigDecimal.valueOf(700));
        book2.setStock(5);

        bookRepository.save(book1);
        bookRepository.save(book2);

        List<Book> books = bookRepository.findAll();

        assertEquals(2, books.size());
        assertTrue(books.stream()
                .anyMatch(book -> book.getTitle().equals("Effective Java")));
        assertTrue(books.stream()
                .anyMatch(book -> book.getTitle().equals("Clean Code")));
    }
}
