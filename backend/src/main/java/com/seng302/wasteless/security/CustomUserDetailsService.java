package com.seng302.wasteless.security;

import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * CustomUserDetailsService is a modified implementation of UserDetailsService
 * Service applies address logic over the UserDetails JPA repository.
 */
public class CustomUserDetailsService implements UserDetailsService {


    @Autowired
    private UserService userService;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userService.findUserByEmail(email);
        return new CustomUserDetails(user);
    }
}
