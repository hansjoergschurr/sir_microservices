package com.example.ProfileService;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class Profile {

    @JsonSetter(nulls = Nulls.SKIP)
    private long id;

    @NotNull(message = "Please provide a 'name'.")
    private String name;

    @NotNull(message = "Please provide an 'email'.")
    @Email(message = "Email not valid.")
    private String email;

    private String description = "";

    public Profile(long id, String name, String email, String description) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.description = description;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
