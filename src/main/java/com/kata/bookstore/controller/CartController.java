package com.kata.bookstore.controller;

import com.kata.bookstore.dto.CartResponse;
import com.kata.bookstore.service.CartService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    private static final Logger log = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        log.debug("Received request to retrieve cart");
        return ResponseEntity.ok(cartService.getCart());
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addToCart(
            @RequestParam Long bookId,
            @RequestParam Integer quantity) {
        log.debug("Received request to add book {} to cart", bookId);

        return ResponseEntity.ok(
                cartService.addToCart(bookId, quantity)
        );
    }

    @PutMapping("/items/{bookId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long bookId,
            @RequestParam Integer quantity) {
        log.debug("Received request to update quantity for book {}", bookId);

        return ResponseEntity.ok(
                cartService.updateQuantity(bookId, quantity)
        );
    }

    @DeleteMapping("/items/{bookId}")
    public ResponseEntity<Void> removeFromCart(
            @PathVariable Long bookId) {
        log.debug("Received request to remove book {} from cart", bookId);

        cartService.removeFromCart(bookId);
        return ResponseEntity.noContent().build();
    }
}
