package org.venity.vgit.services;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserCrudRepository;

import java.security.MessageDigest;
import java.util.Arrays;

@Service
public class UserAuthenticationProviderService implements AuthenticationProvider {
    private final UserCrudRepository userRepository;
    private final ThreadLocal<MessageDigest> passwordDigest;
    private final String INVALID_USERNAME_OR_PASSWORD = "Invalid username or password";

    public UserAuthenticationProviderService(UserCrudRepository userRepository, ThreadLocal<MessageDigest> passwordDigest) {
        this.userRepository = userRepository;
        this.passwordDigest = passwordDigest;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserPrototype userPrototype = userRepository.findByLogin(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException(INVALID_USERNAME_OR_PASSWORD));

        String password = (String) authentication.getCredentials();

        try {
            if (!Arrays.equals(userPrototype
                    .getPasswordHash(), passwordDigest
                    .get()
                    .digest(password.getBytes()))) {
                throw new BadCredentialsException(INVALID_USERNAME_OR_PASSWORD);
            }
        } catch (NullPointerException e) {
            // Ignore
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}
