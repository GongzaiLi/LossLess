package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.PostCardDto;
import com.seng302.wasteless.model.Card;
import com.seng302.wasteless.model.CardSections;
import org.springframework.stereotype.Component;


/**
 * PostCardDtoMapper is used to transform the postCardDto object into Card entity.
 */
@Component
public class PostCardDtoMapper {

    public static Card postCardDtoToEntityMapper (PostCardDto postCardDto) {
        return new Card()
                .setSection(CardSections.fromString(postCardDto.getSection()))
                .setTitle(postCardDto.getTitle())
                .setKeywords(postCardDto.getKeywords())
                .setDescription(postCardDto.getDescription());
    }
}
