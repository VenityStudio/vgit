package org.venity.vgit.configuration;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.venity.vgit.filters.AuthorizationFilter;
import org.venity.vgit.filters.GitURLFilter;
import org.venity.vgit.repositories.UserCrudRepository;
import org.venity.vgit.services.JWTService;
import org.venity.vgit.services.UserAuthenticationProviderService;

@Configuration
public class FilterConfiguration {
    public static final String USER_SESSION_KEY = "user-session";

    @Bean
    public FilterRegistrationBean<AuthorizationFilter> authorizationFilterRegistrationBean(
            JWTService jwtService, UserCrudRepository userRepository,
            UserAuthenticationProviderService userAuthenticationProviderService) {
        var filterRegistrationBean = new FilterRegistrationBean<>(
                new AuthorizationFilter(jwtService, userRepository, userAuthenticationProviderService));
        filterRegistrationBean.setOrder(-101);

        return filterRegistrationBean;
    }

    @Bean
    FilterRegistrationBean<GitURLFilter> gitURLFilterRegistrationBean() {
        var registrationBean = new FilterRegistrationBean<>(new GitURLFilter());
        registrationBean.setOrder(-99);

        return registrationBean;
    }
}
