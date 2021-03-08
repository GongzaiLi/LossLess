package org.seng302.example.Logins;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Logins {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Long id;

    @Column(name = "email") // map camelcase name (java) to snake case (SQL)
    @NotBlank(message = "email is mandatory")
    private String email;

    @NotBlank(message = "password is mandatory")
    @Column(name = "password") // map camelcase name (java) to snake case (SQL)
    private String password;

    @Column(name = "salt") // map camelcase name (java) to snake case (SQL)
    private String salt;

    /**
     * Convenience constructor (it's discouraged to expose the JPA id field)
     *
     * @param email users email
     * @param password users hashed password
     * @param salt users salt value
     */
    public Logins(String email, String password, String salt) {
        this.email = email;
        this.password = password;
        this.salt = salt;
    }
}

