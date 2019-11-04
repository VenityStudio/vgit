package org.venity.vgit.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.venity.vgit.services.UserAuthenticationProviderService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final UserAuthenticationProviderService userAuthenticationProviderService;

    public SecurityConfiguration(UserAuthenticationProviderService userAuthenticationProviderService) {
        this.userAuthenticationProviderService = userAuthenticationProviderService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(userAuthenticationProviderService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .httpBasic()
        .and()
                .authorizeRequests()
                .antMatchers("/git/**") // TODO: make dynamic git urls ...
                .authenticated();
    }
}
