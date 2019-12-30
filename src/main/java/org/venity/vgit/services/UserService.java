package org.venity.vgit.services;

import lombok.Getter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.UserAlreadyExistsException;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserCrudRepository;

import java.security.MessageDigest;

import static org.venity.vgit.VGitRegex.*;

@Service
public class UserService implements AuthenticationProvider {
    private final UserCrudRepository userRepositories;
    private final ThreadLocal<MessageDigest> passwordDigest;

    public UserService(UserCrudRepository userRepositories, ThreadLocal<MessageDigest> passwordDigest) {
        this.userRepositories = userRepositories;
        this.passwordDigest = passwordDigest;
    }

    public void register(String login, String fullName, String email, String password) throws InvalidFormatException, UserAlreadyExistsException {
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
        var name = authentication.getName();
        var password = authentication.getCredentials();

        if (!(password instanceof String)) {
            throw new BadCredentialsException("Invalid username or password");
        }

        var passwordBytes = ((String) password).getBytes();
        var passwordHash = passwordDigest.get().digest(passwordBytes);
        var userPrototype = userRepositories
                .findLoginAndPasswordHash(name, passwordHash)
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        return new UserAuthenticationToken(name, password, userPrototype);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }

    public static class UserAuthenticationToken extends UsernamePasswordAuthenticationToken {

        @Getter
        private UserPrototype userPrototype;

        public UserAuthenticationToken(Object principal, Object credentials, UserPrototype userPrototype) {
            super(principal, credentials);
            this.userPrototype = userPrototype;
        }
    }
}
