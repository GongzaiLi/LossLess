package com.seng302.wasteless.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Dto for post message endpoint.
 */

@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class PostMessageDto {
    @NotNull(message = "Card ID is mandatory")
    private Integer cardId;

    @Column(name = "receiver_id")
    @NotNull(message = "Receiver ID is mandatory")
    private Integer receiverId;

    //Sender ID is implied by currently logged in user

    @NotEmpty(message = "Message is mandatory")
    @Size(max = 250)
    private String messageText;
}
