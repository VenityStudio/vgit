package org.venity.vgit.controllers;

import org.venity.vgit.prototypes.UserPrototype;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

import static org.venity.vgit.configuration.FilterConfiguration.USER_SESSION_KEY;

abstract public class AbstractController {

    protected Optional<UserPrototype> getAuthorization(HttpServletRequest request) {
        return Optional.ofNullable((UserPrototype) request
                .getAttribute(USER_SESSION_KEY));
    }
}
