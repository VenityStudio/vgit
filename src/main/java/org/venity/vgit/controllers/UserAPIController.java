package org.venity.vgit.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.venity.vgit.exceptions.AuthorizationException;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.UserAlreadyExistsException;
import org.venity.vgit.exceptions.UserDoesntExistsException;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserCrudRepository;
import org.venity.vgit.services.JWTService;
import org.venity.vgit.services.UserAuthenticationProviderService;
import org.venity.vgit.services.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserAPIController extends AbstractController {
    private final UserCrudRepository userRepository;
    private final UserService userService;
    private final UserAuthenticationProviderService userAuthenticationProviderService;
    private final JWTService jwtService;

    public UserAPIController(UserCrudRepository userRepository, UserService userService, UserAuthenticationProviderService userAuthenticationProviderService, JWTService jwtService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.userAuthenticationProviderService = userAuthenticationProviderService;
        this.jwtService = jwtService;
    }

    @GetMapping("/{id}")
    public UserPrototype getUser(@PathVariable String id) throws InvalidFormatException, UserDoesntExistsException {
        try {
            return userRepository
                    .findById(Integer.parseInt(id))
                    .orElseThrow(UserDoesntExistsException::new);
        } catch (NumberFormatException e) {
            throw new InvalidFormatException();
        }
    }

    @GetMapping
    public UserPrototype getCurrentUser(HttpServletRequest request) throws AuthorizationException {
        return getAuthorization(request)
                .orElseThrow(AuthorizationException::new);
    }

    @PostMapping("/register")
    public void registerUser(@RequestBody RegisterBody body)
            throws UserAlreadyExistsException, InvalidFormatException {
        try {
            userService.register(body.getLogin(), body.getFullName(), body.getEmail(), body.getPassword());
        } catch (NullPointerException e) {
            throw new InvalidFormatException();
        }
    }

    @PostMapping("/authenticate")
    public Map<String, String> authenticateUser(@RequestBody AuthenticateBody body)
            throws InvalidFormatException, UserDoesntExistsException {
        try {
            userAuthenticationProviderService.authenticate(
                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword()));
        } catch (AuthenticationException e) {
            throw new InvalidFormatException();
        }

        UserPrototype userPrototype = userRepository.findByLogin(body.getUsername())
                .orElseThrow(UserDoesntExistsException::new);

        return Collections.singletonMap("token", jwtService.generateToken(userPrototype));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class RegisterBody {
        private String login;
        private String email;
        private String fullName;
        private String password;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class AuthenticateBody {
        private String username;
        private String password;
    }
}
