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

    private Integer receiverId;

    private Integer senderId;

    private List<Message> messages;

    public GetMessageDto(Integer cardId, Integer receiverId, Integer senderId, List<Message> messages) {
        this.cardId = cardId;
        this.receiverId = receiverId;
        this.senderId = senderId;
        this.messages = messages;
    }
}
