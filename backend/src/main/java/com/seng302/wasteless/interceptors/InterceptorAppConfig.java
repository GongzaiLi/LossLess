package com.seng302.wasteless.interceptors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorAppConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(actAsBusinessInterceptor());
    }

    /**
     * This seems redundant but is required for the interceptor to be a BEAN.
     * This is so Spring will do it's magic on the interceptor and allow autowired services
     * to be accessed from the interceptor
     */
    @Bean
    public ActAsBusinessInterceptor actAsBusinessInterceptor() {
        return new ActAsBusinessInterceptor();
    }
}
