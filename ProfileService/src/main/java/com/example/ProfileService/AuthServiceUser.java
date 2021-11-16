package com.example.ProfileService;

public class AuthServiceUser {
    private long id;
    private String password;

    public AuthServiceUser(long id) {
        this.id = id;
        this.password = "test";
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
