package com.seng302.wasteless.security;

import com.seng302.wasteless.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * CustomerUserDetails is an modified implementation of Spring's UserDetails.
 * This is used by Spring Security in the WebSecurityConfig
 */
public class CustomUserDetails implements UserDetails {

    private User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Sets the 'username' of the currently logged in CustomUserDetails object.
     * In this case the username of a user is just their email.
     * This method comes in handy when the user changes their email and wants to stay logged in.
     * @param email The email that the username/email shall be set to
     */
    public void setUsername(String email) {
        this.user.setEmail(email);
    }

    public Integer getId() {
        return user.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority(user.getRole().toString());
        return Collections.singletonList(userAuthority);
    }

}
