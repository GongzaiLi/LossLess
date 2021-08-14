package com.seng302.wasteless;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.model.UserRoles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;

public class TestUtils {
    public static Address newThrowawayAddress() {
        var throwawayAddress = new Address();
        throwawayAddress.setCountry("NZ");
        throwawayAddress.setSuburb("Riccarton");
        throwawayAddress.setCity("Christchurch");
        throwawayAddress.setStreetNumber("1");
        throwawayAddress.setStreetName("Ilam Rd");
        throwawayAddress.setPostcode("8041");
        return throwawayAddress;
    }

    public static User newUserWithEmail(String email) {
        var newUser = new User();
        newUser.setRole(UserRoles.USER);
        newUser.setEmail(email);
        newUser.setPassword(new BCryptPasswordEncoder().encode("a"));
        newUser.setDateOfBirth(LocalDate.now().minusYears(17));
        newUser.setBio("Bio");
        newUser.setFirstName("FirstName");
        newUser.setLastName("LastName");
        newUser.setHomeAddress(newThrowawayAddress());
        newUser.setCreated(LocalDate.now());
        return newUser;
    }
}
