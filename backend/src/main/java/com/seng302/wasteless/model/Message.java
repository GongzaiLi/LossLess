package com.seng302.wasteless.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Class representing a Message between users.
 * Messages are related between a user and the card.
 * The other user in the conversation is the card owner and so is implicit.
 */
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
@Entity // declare this class as a JPA entity (that can be mapped to a SQL table)
public class Message {

    @Id // this field (attribute) is the table primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // autoincrement the ID
    private Integer id;

    @Column(name = "card_id")
    @NotNull(message = "Card ID is mandatory")
    private Integer cardId;

    @Column(name = "user_id")
    @NotNull(message = "User ID is mandatory")
    private Integer userId;

    @Column(name = "message_text")
    @NotNull(message = "Message is mandatory")
    @Size(max = 250)
    private String messageText;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

}

