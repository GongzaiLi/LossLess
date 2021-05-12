package com.seng302.wasteless.dto;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

/**
 * An implementation of Login model.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class LoginDto {

    private LoginDto login;

    @NotNull(message = "email is mandatory")
    private String email;

    @NotNull(message = "password is mandatory")
    private String password;

    public LoginDto(String email, String password) {

        this.email = email;
        this.password = password;
    }
}


