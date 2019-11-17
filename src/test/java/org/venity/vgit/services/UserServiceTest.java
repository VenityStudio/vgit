package org.venity.vgit.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.venity.vgit.configuration.CryptoConfiguration;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.UserAlreadyExistsException;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserCrudRepository;

import java.security.MessageDigest;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends Assertions {

    private UserCrudRepository userRepository;
    private ThreadLocal<MessageDigest> passwordDigest;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserCrudRepository.class);
        passwordDigest = new CryptoConfiguration().passwordDigest();
        userService = new UserService(userRepository, passwordDigest);
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(userRepository);
    }

    @Test
    void testThatUserNotRegisteredWhenItExists() {
        Mockito.when(userRepository.existsByLoginOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        try {
            userService.register("TestUser", "Test Testovich", "test@test.by", "SuperSecretPassword1");
            fail();
        } catch (InvalidFormatException e) {
            fail();
        } catch (UserAlreadyExistsException e) {
            // OK!
        }
    }

    @Test
    void testThatUserNotRegisteredWhenInputDataAreInvalid() {
        Mockito.when(userRepository.existsByLoginOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        try {
            userService.register("§", "§", "§", "§");
            fail();
        } catch (UserAlreadyExistsException e) {
            fail();
        } catch (InvalidFormatException e) {
            // OK!
        }
    }

    @Test
    void testThatUserRegisteredWhenAllDataAreValid() {
        Mockito.when(userRepository.existsByLoginOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        try {
            userService.register("test1", "Test Testovich", "maksim182003mit@gmail.com", "SuperSecretPassword1");
        } catch (InvalidFormatException | UserAlreadyExistsException e) {
            fail();
        }

        var validPasswordHash = passwordDigest.get().digest("SuperSecretPassword1".getBytes());
        var validUserPrototype = new UserPrototype();

        validUserPrototype.setLogin("test1");
        validUserPrototype.setFullName("Test Testovich");
        validUserPrototype.setEmail("maksim182003mit@gmail.com");
        validUserPrototype.setPasswordHash(validPasswordHash);

        Mockito.verify(userRepository).save(ArgumentMatchers.argThat(argument -> {
            validUserPrototype.setRepositoriesIds(argument.getRepositoriesIds());

            return validUserPrototype.equals(argument);
        }));
    }
}