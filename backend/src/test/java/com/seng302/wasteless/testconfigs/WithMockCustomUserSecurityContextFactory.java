package com.seng302.wasteless.testconfigs;

import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    @Autowired
    UserService userService;

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        User user = new User();
        user.setRole(customUser.role());
        user.setEmail(customUser.email());
        user.setPassword(customUser.password());
        user.setDateOfBirth(LocalDate.now());
        user.setBio("Bio");
        user.setFirstName("FirstName");
        user.setLastName("LastName");
        user.setHomeAddress("HomeAddress");

        userService.createUser(user);

        CustomUserDetails principal =
                new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        context.setAuthentication(auth);

        return context;
    }
}
