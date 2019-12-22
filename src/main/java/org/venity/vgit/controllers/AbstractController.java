package org.venity.vgit.controllers;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.venity.vgit.exceptions.RedirectException;
import org.venity.vgit.prototypes.UserPrototype;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.venity.vgit.configuration.FilterConfiguration.USER_SESSION_KEY;

abstract public class AbstractController {

    protected Optional<UserPrototype> getAuthorization(HttpServletRequest request) {
        return Optional.ofNullable((UserPrototype) request
                .getAttribute(USER_SESSION_KEY));
    }

    @ExceptionHandler(RedirectException.class)
    public void redirect(HttpServletResponse response, RedirectException exception) throws IOException {
        response.sendRedirect(exception.getUrl());
    }
}
