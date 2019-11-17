package org.venity.vgit.services;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.UserAlreadyExistsException;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserCrudRepository;

import java.security.MessageDigest;
import java.util.HashSet;

import static org.venity.vgit.VGitRegex.*;

@Service
public class UserService {
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

        if (userRepositories.existsByLoginOrEmail(login, email)) {
            throw new UserAlreadyExistsException();
        }

        var userPrototype = new UserPrototype();
        var passwordHash = passwordDigest.get().digest(password.getBytes());

        userPrototype.setLogin(login);
        userPrototype.setFullName(fullName);
        userPrototype.setEmail(email);
        userPrototype.setRepositoriesIds(new HashSet<>());
        userPrototype.setPasswordHash(passwordHash);

        userRepositories.save(userPrototype);
    }
}
