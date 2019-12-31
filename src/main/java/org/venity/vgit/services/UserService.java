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
import java.util.Optional;

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
        Optional<UserPrototype> userPrototype;

        var name = authentication.getName();
        var password = authentication.getCredentials();

        if (password instanceof String) {
            var passwordBytes = ((String) password).getBytes();
            var passwordHash = passwordDigest.get().digest(passwordBytes);

            userPrototype = userRepositories.findByLoginAndPasswordHash(name, passwordHash);
        } else {
            userPrototype = userRepositories.findByLogin(name);
        }

        return new UserAuthenticationToken(
                name,
                password,
                userPrototype.orElseThrow(() -> new BadCredentialsException("Invalid username or password"))
        );
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
