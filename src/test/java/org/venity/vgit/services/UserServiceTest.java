package org.venity.vgit.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.UserAlreadyExistsException;
import org.venity.vgit.repositories.UserRepository;

import java.security.MessageDigest;

@ExtendWith(MockitoExtension.class)
class UserServiceTest extends Assertions {

    @Mock
    public UserRepository userRepository;

    @Mock
    public ThreadLocal<MessageDigest> passwordDigest;

    @InjectMocks
    public UserService userService;

    @Test
    void testThatUserNotRegisteredWhenItExists() {
        Mockito.when(userRepository.existsByLoginOrEmail(Mockito.anyString(), Mockito.anyString())).thenReturn(true);

        try {
            userService.register("mwguy", "Maxim Tarasov", "test@test.by", "test.by");
            fail();
        } catch (InvalidFormatException e) {
            fail();
        } catch (UserAlreadyExistsException e) {
            // OK!
        }
    }
}