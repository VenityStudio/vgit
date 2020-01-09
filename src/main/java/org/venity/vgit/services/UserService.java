package org.venity.vgit.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.venity.vgit.VGitRegex.*;

@Service
public class UserService implements AuthenticationProvider {
    private final UserCrudRepository userRepositories;
    private final ThreadLocal<MessageDigest> passwordDigest;

    public UserService(UserCrudRepository userRepositories, ThreadLocal<MessageDigest> passwordDigest) {
        this.userRepositories = userRepositories;
        this.passwordDigest = passwordDigest;
    }

    public void register(String login, String fullName, String email, String password)
            throws InvalidFormatException, UserAlreadyExistsException {
        checkField(LOGIN_PATTERN, login, "Invalid login format!", false);
        checkField(EMAIL_PATTERN, email, "Invalid E-Mail format!", false);
        checkField(FULL_NAME_PATTERN, fullName, "Invalid full name format!", false);
        checkField(PASSWORD_PATTERN, password, "Invalid password format!", false);

        // TODO: Implement with unique keys
        if (userRepositories.existsByLoginOrEmail(login, email))
            throw new UserAlreadyExistsException();

        var userPrototype = new UserPrototype();
        var passwordHash = passwordDigest.get().digest(password.getBytes());

        userPrototype.setGender(UserPrototype.Gender.UNDEFINED);
        userPrototype.setLogin(login);
        userPrototype.setEmail(email);
        userPrototype.setFullName(fullName);
        userPrototype.setPasswordHash(passwordHash);
        userPrototype.setCreationDate(LocalDateTime.now());
        userPrototype.setLastUpdateDate(userPrototype.getCreationDate());

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

    public UserPrototype edit(UserPrototype original, UserEditData data) throws InvalidFormatException {
        checkField(EMAIL_PATTERN, data.getEmail(), "Invalid E-Mail format!", true);
        checkField(FULL_NAME_PATTERN, data.getFullName(), "Invalid full name format!", true);
        checkField(PASSWORD_PATTERN, data.getPassword(), "Invalid password format!", true);

        original.setFullName(ifNotNullReturn(data.getFullName(), original.getFullName()));
        original.setGender(ifNotNullReturn(data.getGender(), original.getGender()));
        original.setStatus(ifNotNullReturn(data.getStatus(), original.getStatus()));
        original.setBio(ifNotNullReturn(data.getBio(), original.getBio()));
        original.setEmail(ifNotNullReturn(data.getEmail(), original.getEmail()));

        var password = data.getPassword();
        if (password != null) {
            original.setPasswordHash(passwordDigest.get().digest(password.getBytes()));
        }

        original.setLastUpdateDate(LocalDateTime.now());
        return userRepositories.save(original);
    }

    private void checkField(Pattern pattern, String field, String message, boolean consumeNull)
            throws InvalidFormatException {
        if (field == null) {
            if (consumeNull) return;

            throw new NullPointerException();
        }

        if (!pattern.matcher(field).matches())
            throw new InvalidFormatException(message);
    }

    private <T> T ifNotNullReturn(T value, T defaultValue) {
        return value != null ? value : defaultValue;
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserEditData {
        private UserPrototype.Gender gender;
        private String status;
        private String email;
        private String fullName;
        private String password;
        private String bio;
    }
}
