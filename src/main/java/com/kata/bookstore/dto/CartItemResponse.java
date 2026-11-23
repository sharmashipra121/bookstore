package com.kata.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;
    private Long bookId;
    private String title;
    private String author;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}
