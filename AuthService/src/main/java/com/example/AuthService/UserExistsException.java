package com.example.AuthService;


public class UserExistsException extends RuntimeException {

   UserExistsException(Long id) {
        super("User already exists: " + id);
    }
}
