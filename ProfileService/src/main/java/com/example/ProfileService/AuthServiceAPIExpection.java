package com.example.ProfileService;

public class AuthServiceAPIExpection extends RuntimeException {
    public AuthServiceAPIExpection() {
        super("Unexpected behavior of the Auth Service.");
    }
}
