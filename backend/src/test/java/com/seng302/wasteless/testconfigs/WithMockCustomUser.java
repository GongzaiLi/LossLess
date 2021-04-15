package com.seng302.wasteless.testconfigs;

import com.seng302.wasteless.model.UserRoles;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContextFactory.class)
public @interface WithMockCustomUser {

    String email() default "user@700";

    UserRoles role() default UserRoles.USER;

    String password() default "password";
}
