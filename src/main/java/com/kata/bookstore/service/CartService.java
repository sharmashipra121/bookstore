package com.kata.bookstore.service;

import com.kata.bookstore.dto.CartResponse;

public interface CartService {

    CartResponse getCart();

    CartResponse addToCart(Long bookId, Integer quantity);

    CartResponse updateQuantity(Long bookId, Integer quantity);

    void removeFromCart(Long bookId);
}
