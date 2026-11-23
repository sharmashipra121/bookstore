package com.kata.bookstore.controller;

import com.kata.bookstore.dto.CartResponse;
import com.kata.bookstore.service.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @RequestParam Long bookId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                cartService.addToCart(bookId, quantity)
        );
    }

    @PutMapping("/items/{bookId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long bookId,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                cartService.updateQuantity(bookId, quantity)
        );
    }

    @DeleteMapping("/items/{bookId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long bookId) {

        cartService.removeFromCart(bookId);
        return ResponseEntity.noContent().build();
    }
}
