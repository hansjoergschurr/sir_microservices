package com.example.AuthService;

import javax.validation.constraints.NotNull;
public class NewUser {

    @NotNull(message = "Id required.")
    private long id;

    @NotNull(message = "Password required.")
    private String password;

    public NewUser(long id, String password) {
        this.id = id;
        this.password = password;
    }

    public long getId() {
        return this.id;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean checkPassword(String password) {
        return (this.password.equals(password));
    }

}
