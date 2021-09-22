package com.seng302.wasteless.dto;

import com.seng302.wasteless.model.Message;
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

    private Integer cardOwnerId;

    private Integer otherUserId;

    private List<Message> messages;

    public GetMessageDto(Integer cardId, Integer otherUserId, Integer cardOwnerId, List<Message> messages) {
        this.cardId = cardId;
        this.cardOwnerId = cardOwnerId;
        this.otherUserId = otherUserId;
        this.messages = messages;
    }
}
