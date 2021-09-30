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

    private String email;
    private String password;
    private String role;
    private Integer id;

    public CustomUserDetails(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.role = user.getRole().toString();
        this.id = user.getId();
    }

    @Override
    public String getUsername() { return this.email; }

    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Sets the 'username' of the currently logged in CustomUserDetails object.
     * In this case the username of a user is just their email.
     * This method comes in handy when the user changes their email and wants to stay logged in.
     * @param email The email that the username/email shall be set to
     */
    public void setUsername(String email) {
        this.email = email;
    }

    public Integer getId() {
        return this.id;
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
        SimpleGrantedAuthority userAuthority = new SimpleGrantedAuthority(this.role);
        return Collections.singletonList(userAuthority);
    }

}
