package org.venity.vgit.controllers;

import org.springframework.web.bind.annotation.*;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.UserAlreadyExistsException;
import org.venity.vgit.exceptions.UserDoesntExistsException;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserRepository;
import org.venity.vgit.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
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

    @PostMapping("/register")
    public void registerUser(String login, String fullName, String email, String password)
            throws UserAlreadyExistsException, InvalidFormatException {
        try {
            userService.register(login, fullName, email, password);
        } catch (NullPointerException e) {
            throw new InvalidFormatException();
        }
    }
}
