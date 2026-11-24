package com.kata.bookstore.controller;

import com.kata.bookstore.dto.BookResponse;
import com.kata.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BookController(bookService)).build();
    }

    @Test
    void shouldReturnPagedBookDtos() throws Exception {
        BookResponse book = BookResponse.builder()
                .id(1L)
                .title("Effective Java")
                .author("Joshua Bloch")
                .price(BigDecimal.valueOf(650))
                .stock(10)
                .build();
        PageRequest pageable = PageRequest.of(1, 2, Sort.by("id").ascending());
        when(bookService.getAllBooks(pageable))
                .thenReturn(new PageImpl<>(List.of(book), pageable, 3));

        mockMvc.perform(get("/books").param("page", "1").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.content[0].price").value(650))
                .andExpect(jsonPath("$.page.number").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(3));

        verify(bookService).getAllBooks(pageable);
    }

    @Test
    void shouldCapPageSizeAtOneHundred() throws Exception {
        when(bookService.getAllBooks(any()))
                .thenReturn(Page.empty(PageRequest.of(0, 100, Sort.by("id").ascending())));

        mockMvc.perform(get("/books").param("size", "500"))
                .andExpect(status().isOk());

        verify(bookService).getAllBooks(PageRequest.of(0, 100, Sort.by("id").ascending()));
    }

    @Test
    void shouldRejectInvalidPageParameters() throws Exception {
        mockMvc.perform(get("/books").param("page", "-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldHandleConcurrentBookRequests() throws Exception {
        when(bookService.getAllBooks(any()))
                .thenReturn(Page.empty(PageRequest.of(0, 20, Sort.by("id").ascending())));

        int requestCount = 12;
        CountDownLatch start = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(requestCount);
        try {
            List<Future<Integer>> responses = java.util.stream.IntStream.range(0, requestCount)
                    .mapToObj(index -> executor.submit(() -> {
                        start.await();
                        return mockMvc.perform(get("/books"))
                                .andReturn()
                                .getResponse()
                                .getStatus();
                    }))
                    .toList();

            start.countDown();
            for (Future<Integer> response : responses) {
                assertEquals(200, response.get());
            }
        } finally {
            executor.shutdownNow();
        }
    }
}
