package com.example.AuthService;

public class PasswordIncorrectException extends RuntimeException {
    PasswordIncorrectException(Long id) {
        super("Password incorrect. For user: " + id);
    }
}