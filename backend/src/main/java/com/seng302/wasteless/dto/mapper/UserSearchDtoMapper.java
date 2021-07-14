package com.seng302.wasteless.dto.mapper;

import com.seng302.wasteless.dto.GetUserDto;
import com.seng302.wasteless.dto.UserSearchDto;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class UserSearchDtoMapper {
    private static UserService userService;

    @Autowired
    public UserSearchDtoMapper(UserService userService) {
        UserSearchDtoMapper.userService = userService;
    }
    public static UserSearchDto toGetUserSearchDto(String searchQuery, Integer count, Integer offset) {

        List<User> searchResults = new ArrayList<>(userService.searchForMatchingUsers(searchQuery));
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
