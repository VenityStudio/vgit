package org.venity.vgit.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.venity.vgit.filters.AuthorizationFilter;
import org.venity.vgit.repositories.UserRepository;
import org.venity.vgit.services.JWTService;

@Configuration
public class FilterConfiguration {
    public static final String USER_SESSION_KEY = "user-session";

    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilterRegistrationBean(JWTService jwtService, UserRepository userRepository) {
        var filterRegistrationBean = new FilterRegistrationBean<>(new AuthorizationFilter(jwtService, userRepository));
        filterRegistrationBean.setOrder(-101);

        return filterRegistrationBean;
    }
}
