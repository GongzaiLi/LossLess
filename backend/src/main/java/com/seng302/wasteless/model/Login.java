package com.seng302.wasteless.model;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class Login {

    private Login login;

    @NotNull(message = "email is mandatory")
    private String email;

    @NotNull(message = "password is mandatory")
    private String password;

    public Login(String email, String password) {

        this.email = email;
        this.password = password;
    }
}


