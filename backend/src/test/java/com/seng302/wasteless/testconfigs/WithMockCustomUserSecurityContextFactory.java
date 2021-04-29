package com.seng302.wasteless.testconfigs;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.security.CustomUserDetails;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.AddressService;
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

    @Autowired
    AddressService addressService;

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

        Address address = new Address();
        address.setCountry("NZ");
        address.setCity("Christchurch");
        address.setStreetNumber("1");
        address.setStreetName("Ilam Rd");
        address.setPostcode("8041");

        addressService.createAddress(address);
        user.setHomeAddress(address);
        user.setCreated(LocalDate.now());

        userService.createUser(user);

        CustomUserDetails principal =
                new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, "password", principal.getAuthorities());
        context.setAuthentication(auth);

        return context;
    }
}
