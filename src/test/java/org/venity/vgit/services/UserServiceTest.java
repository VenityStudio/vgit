package org.venity.vgit.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
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
    void testRegisteringWhenReceivedInvalidData() {
        assertThrows(InvalidFormatException.class, () ->
                userService.register("§", "§", "§", "§")
        );

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testRegisteringWhenUserCreated() {
        Mockito.when(userRepository.existsByLoginOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () ->
                userService.register("test1", "Test Testovich", "maksim182003mit@gmail.com", "SuperSecretPassword1")
        );

        Mockito.verify(userRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void testRegisteringWhenAllInNormal() throws UserAlreadyExistsException, InvalidFormatException {
        Mockito.when(userRepository.existsByLoginOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(false);

        userService.register("test1", "Test Testovich", "maksim182003mit@gmail.com", "SuperSecretPassword1");

        Mockito.verify(userRepository).save(ArgumentMatchers.argThat(argument -> {
            var validUserPrototype = new UserPrototype();

            validUserPrototype.setLogin("test1");
            validUserPrototype.setFullName("Test Testovich");
            validUserPrototype.setEmail("maksim182003mit@gmail.com");
            validUserPrototype.setPasswordHash(passwordDigest.get().digest("SuperSecretPassword1".getBytes()));

            return validUserPrototype.equals(argument);
        }));
    }
}