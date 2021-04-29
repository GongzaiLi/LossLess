package com.seng302.wasteless.interceptors;

import com.seng302.wasteless.MainApplicationRunner;
import com.seng302.wasteless.model.User;
import com.seng302.wasteless.service.BusinessService;
import com.seng302.wasteless.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Interceptor (ie 'middleware') for authorizing users acting as a business.
 * Will return a 403 and halt the request if the user's X-Business-Acting-As header
 * references a business they are not authorized as an admin of
 */
public class ActAsBusinessInterceptor implements HandlerInterceptor {
    private static final Logger logger = LogManager.getLogger(MainApplicationRunner.class.getName());

    @Autowired
    UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) { // Not our problem if they're not signed in
            return true;
        }
        String currentPrincipalEmail = authentication.getName();

        User user = userService.findUserByEmail(currentPrincipalEmail);

        String businessActingAsString = request.getHeader("X-Business-Acting-As");

        boolean isActingAsValidBusiness;
        if (user == null || businessActingAsString == null) { // Not our problem if they're not signed in, or not acting as anyone
            isActingAsValidBusiness = true;
        } else {
            try {
                Integer businessActingAsId = Integer.parseInt(businessActingAsString);
                isActingAsValidBusiness = userService.checkUserAdminsBusiness(businessActingAsId, user.getId());
            } catch (NumberFormatException e) {
                isActingAsValidBusiness = false;
            }
        }


        if (! isActingAsValidBusiness) {
            logger.warn("User {} tried to act as business {} which they are not an admin of", user.getId(), businessActingAsString);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(String.format("You are acting as business %s but you are not an admin of the business", businessActingAsString));
        }

        return isActingAsValidBusiness;
    }
}
