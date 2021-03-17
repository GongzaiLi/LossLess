package com.seng302.wasteless.User;

import lombok.Data;
import lombok.ToString;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;

@Data // generate setters and getters for all fields (lombok pre-processor)
@ToString // generate a toString method
public class Login {

    @NotNull(message = "email is mandatory")
    private String email;

    @NotNull(message = "password is mandatory")
    private String password;

}


