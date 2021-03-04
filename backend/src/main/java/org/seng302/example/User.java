package org.seng302.example;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class User {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Long id;

    @Column(name = "first_name") // map camelcase name (java) to snake case (SQL)
    private String firstName;

    @Column(name = "last_name") // map camelcase name (java) to snake case (SQL)
    private String lastName;

    @Column(name = "middle_name") // map camelcase name (java) to snake case (SQL)
    private String middleName;

    @Column(name = "nick_name") // map camelcase name (java) to snake case (SQL)
    private String nickname;

    @Column(name = "email") // map camelcase name (java) to snake case (SQL)
    private String email;

    @Column(name = "date_of_birth") // map camelcase name (java) to snake case (SQL)
    private String dateOfBirth;

    @Column(name = "home_address") // map camelcase name (java) to snake case (SQL)
    private String homeAddress;


    public User() {
    }

    /**
     * Convenience constructor (it's discouraged to expose the JPA id field)
     *
     * @param firstName student first name
     * @param lastName  student last name
     */
    public User(String firstName, String lastName, String email, String dateOfBirth, String homeAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.dateOfBirth = dateOfBirth;
        this.homeAddress = homeAddress;
    }
}

