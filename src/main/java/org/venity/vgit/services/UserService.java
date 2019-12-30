package org.venity.vgit.services;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.UserAlreadyExistsException;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserCrudRepository;

import java.security.MessageDigest;
import java.util.Collection;

import static org.venity.vgit.VGitRegex.*;

@Service
public class UserService implements AuthenticationProvider {
    private final UserCrudRepository userRepositories;
    private final ThreadLocal<MessageDigest> passwordDigest;

    public UserService(UserCrudRepository userRepositories, ThreadLocal<MessageDigest> passwordDigest) {
        this.userRepositories = userRepositories;
        this.passwordDigest = passwordDigest;
    }

    public void register(
            @NonNull String login,
            @NonNull String fullName,
            @NonNull String email,
            @NonNull String password
    ) throws InvalidFormatException, UserAlreadyExistsException {

        if (!LOGIN_PATTERN.matcher(login).matches() ||
                !EMAIL_PATTERN.matcher(email).matches() ||
                !FULLNAME_PATTERN.matcher(fullName).matches() ||
                !PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidFormatException();
        }

        // TODO: Implement with unique keys
        if (userRepositories.existsByLoginOrEmail(login, email)) {
            throw new UserAlreadyExistsException();
        }

        var userPrototype = new UserPrototype();
        var passwordHash = passwordDigest.get().digest(password.getBytes());

        userPrototype.setLogin(login);
        userPrototype.setEmail(email);
        userPrototype.setFullName(fullName);
        userPrototype.setPasswordHash(passwordHash);

        userRepositories.save(userPrototype);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var password = authentication.getCredentials();

        if (password instanceof String) {
            var passwordBytes = ((String) password).getBytes();
            var passwordHash = passwordDigest.get().digest(passwordBytes);

            if (userRepositories.existsLoginAndPasswordHash(authentication.getName(), passwordHash)) {
                throw new BadCredentialsException("Invalid username or password");
            }
        }

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    public static class UserAuthenticationToken extends UsernamePasswordAuthenticationToken {

        @Getter
        @Setter
        private UserPrototype userPrototype;

        public UserAuthenticationToken(Object principal, Object credentials) {
            super(principal, credentials);
        }

        public UserAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
            super(principal, credentials, authorities);
        }
    }
}
