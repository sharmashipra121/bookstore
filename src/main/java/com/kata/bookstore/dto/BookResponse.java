package com.kata.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BookResponse {
    private Long id;
    private String title;
    private String author;
    private BigDecimal price;
    private Integer stock;
}
