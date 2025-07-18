package com.rental.houserental.exceptions.wishlist;

public class WishlistOperationException extends RuntimeException {
    public WishlistOperationException(String message) {
        super(message);
    }
    public WishlistOperationException(String message, Throwable cause) {
        super(message, cause);
    }
} 