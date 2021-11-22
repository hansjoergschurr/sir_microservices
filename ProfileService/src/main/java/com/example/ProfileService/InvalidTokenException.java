package com.example.ProfileService;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(
            long user_id,
            String invalid_token)
    {
        super(String.format("The token %s is not a valid token for the user %d.", invalid_token, user_id));
    }
}
