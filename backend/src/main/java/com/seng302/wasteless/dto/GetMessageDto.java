package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Message;
import com.seng302.wasteless.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Dto for get message endpoint.
 */

@Accessors(chain = true) //Allows chaining of getters and setters
@Data // generate setters and getters for all fields (lombok pre-processor)
@NoArgsConstructor // generate a no-args constructor needed by JPA (lombok pre-processor)
@ToString // generate a toString method
public class GetMessageDto {

    private Integer cardId;

    private User cardOwner;

    private User otherUser;

    private List<Message> messages;

    public GetMessageDto(Integer cardId, User otherUser, User cardOwner, List<Message> messages) {
        this.cardId = cardId;
        this.otherUser = otherUser;
        this.cardOwner = cardOwner;
        this.messages = messages;
    }
}
