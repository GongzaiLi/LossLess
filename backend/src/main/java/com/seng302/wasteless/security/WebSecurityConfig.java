package com.seng302.wasteless.security;

import com.seng302.wasteless.model.UserRoles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.savedrequest.NullRequestCache;

/**
 * WebSecurityConfig allows customization to Spring's WebSecurity.
 * Used for enabling spring web security and creates configuration.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * Bean which reveals authentication manager to rest of app.
     *
     * @return authentication manager.
     * @throws Exception exception.
     */
    @Bean(name = "authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * Bean which provides user detail service for getting user details.
     *
     * @return new AppUserDetailsService object.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    /**
     * Bean which provides password encoder.
     *
     * @return new BCryptPasswordEncoder object.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates and returns the authentication provider to be used when authenticating user credentials.
     * This authentication provider will retrieve user details from the database through the user details service.
     *
     * @return the authentication provider used by the authentication manager.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Configuration for authentication manager.
     *
     * @param auth authentication manager builder.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     * Configures security for HTTP.
     *
     * @param http HTTP security configuration.
     * @throws Exception exception.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String[] publicRoutes = {"/login", "/users", "/h2/**"};
        http
                .cors()
                .and().csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringAntMatchers(publicRoutes)
                .and()
                .authorizeRequests()
                    .antMatchers(publicRoutes).permitAll()
                    .antMatchers("/users/{\\d+}/makeAdmin", "/users/{\\d+}/revokeAdmin")
                        .hasAnyAuthority(UserRoles.GLOBAL_APPLICATION_ADMIN.toString(), UserRoles.DEFAULT_GLOBAL_APPLICATION_ADMIN.toString())
                    .anyRequest().authenticated()
                .and()
                .headers().frameOptions().disable()
                .and()
                .requestCache().requestCache(new NullRequestCache())
                .and()
                .formLogin().disable()
                // We want to return a 401 UnAuthorized if there are issues with csrf (eg no csrf token) by default,
                // but Spring Security will return 403 Forbidden. This bit changes the default from 403 to 401
                .exceptionHandling()
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));

        http.csrf().disable();// when using the postman.

//                .logout() //Can call '/logout' to log out
//                .permitAll()
//                .invalidateHttpSession(true)
//                .deleteCookies("JSESSIONID")
//                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))

        ;


    }
}