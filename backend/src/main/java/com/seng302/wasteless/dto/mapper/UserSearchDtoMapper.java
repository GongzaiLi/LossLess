package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.GetUserDto;
import com.seng302.wasteless.dto.UserSearchDto;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * UserSearchDtoMapper is used to transform a search query into a UserSearchDto object.
 * Transforms the set from the search to a list then applies an offset and count to the search to work with pagination
 * then returns a UserSearchDto object containing the reduced list and the number of total results for the search query
 */
@Component
public class UserSearchDtoMapper {
    private static UserService userService;

    @Autowired
    public UserSearchDtoMapper(UserService userService) {
        UserSearchDtoMapper.userService = userService;
    }

    /**
     * UserSearchDtoMapper is used to transform a search query into a UserSearchDto object.
     * Transforms the set from the search to a list then applies an offset and count to the search to work with pagination
     * then returns a UserSearchDto object containing the reduced list and the number of total results for the search query.
     *
     * Checks if count or offset are negative, if either are return empty UserSearchDto. These should be validated
     * before this method. The checks present here are a last line of defense against bad input.
     *
     * @param searchQuery       The query string to search for in users
     * @param offset            The offset of the search (how many results to 'skip'). Should be >= 0
     * @param count             The number of results to return. Should be >= 0
     * @return                  List of users who match the search query, maximum length of count, offset by offset.
     */
    public static UserSearchDto toGetUserSearchDto(String searchQuery, Integer count, Integer offset) {

        if (count < 0 || offset < 0) {
            return new UserSearchDto();
        }

        List<User> searchResults = userService.searchForMatchingUsers(searchQuery);
        List<GetUserDto> searchResultsDto = new ArrayList<>();
        int end = offset+count;
        if(offset<searchResults.size()) {
            if (end>searchResults.size()){end=searchResults.size();}
            for (User user : searchResults.subList(offset,end)) {
                searchResultsDto.add(GetUserDtoMapper.toGetUserDto(user));
            }
        }
        return new UserSearchDto()
                .setResults(searchResultsDto)
                .setTotalItems(searchResults.size());
    }
}
