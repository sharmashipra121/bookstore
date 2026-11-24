package com.kata.bookstore.service.impl;

import com.kata.bookstore.dto.CartItemResponse;
import com.kata.bookstore.dto.CartResponse;
import com.kata.bookstore.entity.Book;
import com.kata.bookstore.entity.Cart;
import com.kata.bookstore.entity.CartItem;
import com.kata.bookstore.entity.User;
import com.kata.bookstore.repository.BookRepository;
import com.kata.bookstore.repository.CartRepository;
import com.kata.bookstore.repository.UserRepository;
import com.kata.bookstore.service.CartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private static final Logger log = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(
            CartRepository cartRepository,
            BookRepository bookRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedInUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication().getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUser(user);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public CartResponse getCart() {
        User user = getLoggedInUser();
        Cart cart = getOrCreateCart(user);

        log.debug("Retrieved cart with {} items", cart.getItems().size());
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse addToCart(Long bookId, Integer quantity) {
        validateQuantity(quantity);

        User user = getLoggedInUser();
        Cart cart = getOrCreateCart(user);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .orElse(null);

        int newQuantity = quantity + (existingItem == null ? 0 : existingItem.getQuantity());

        if (newQuantity > book.getStock()) {
            log.warn("Rejected cart addition because requested quantity exceeds stock for book {}", bookId);
            throw new IllegalArgumentException("Insufficient stock available");
        }

        if (existingItem != null) {
            existingItem.setQuantity(newQuantity);
        } else {
            cart.getItems().add(createCartItem(cart, book, quantity));
        }

        cartRepository.save(cart);

        log.info("Added book {} to cart; resulting quantity is {}", bookId, newQuantity);
        return mapToCartResponse(cart);
    }

    @Override
    public CartResponse updateQuantity(Long bookId, Integer quantity) {
        validateQuantity(quantity);

        User user = getLoggedInUser();
        Cart cart = getOrCreateCart(user);

        CartItem item = findCartItem(cart, bookId);

        if (quantity > item.getBook().getStock()) {
            log.warn("Rejected cart quantity update because requested quantity exceeds stock for book {}", bookId);
            throw new IllegalArgumentException("Insufficient stock available");
        }

        item.setQuantity(quantity);

        log.info("Updated cart quantity for book {} to {}", bookId, quantity);
        return mapToCartResponse(cart);
    }

    @Override
    public void removeFromCart(Long bookId) {
        User user = getLoggedInUser();
        Cart cart = getOrCreateCart(user);
        cart.getItems().remove(findCartItem(cart, bookId));
        log.info("Removed book {} from cart", bookId);
    }

    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity <= 0) {
            log.warn("Rejected cart operation because quantity must be greater than zero");
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    private CartItem findCartItem(Cart cart, Long bookId) {
        return cart.getItems().stream()
                .filter(item -> item.getBook().getId().equals(bookId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Book not found in cart"));
    }

    private CartItem createCartItem(Cart cart, Book book, Integer quantity) {
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setBook(book);
        item.setQuantity(quantity);
        return item;
    }

    private CartResponse mapToCartResponse(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : cart.getItems()) {
            Book book = item.getBook();
            BigDecimal subtotal = book.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            totalPrice = totalPrice.add(subtotal);
            totalItems += item.getQuantity();

            items.add(new CartItemResponse(
                    item.getId(),
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getPrice(),
                    item.getQuantity(),
                    subtotal
            ));
        }

        return new CartResponse(
                cart.getId(),
                items,
                totalPrice,
                totalItems
        );
    }
}
