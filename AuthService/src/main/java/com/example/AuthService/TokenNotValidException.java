package com.example.AuthService;

public class TokenNotValidException extends RuntimeException {
    TokenNotValidException(String token) {
        super("Token not valid: " + token);
    }
}