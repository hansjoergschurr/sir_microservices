package com.example.AuthService;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Random;

public class Token {

    private String token;
    private long userId;
    private LocalDateTime timeLimit;

    final char[] allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private String generateToken() {
        Random random = new SecureRandom();
        StringBuilder token = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            token.append(allowedChars[random.nextInt(allowedChars.length)]);
        }

        return token.toString();
    }

    public Token(long userId) {
        this.userId = userId;
        this.token = this.generateToken();
        this.timeLimit = LocalDateTime.now().plusMinutes(5);
    }

    public long getUserId() {
        return this.userId;
    }

    public String getToken() {
        return this.token;
    }

    public LocalDateTime getTimeLimit() {
        return this.getTimeLimit();
    }

    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return this.timeLimit.isAfter(now);
    }

}
