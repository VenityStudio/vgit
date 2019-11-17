package org.venity.vgit.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.venity.vgit.exceptions.AuthorizationException;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.RepositoryAlreadyExistsException;
import org.venity.vgit.exceptions.RepositoryCreateException;
import org.venity.vgit.prototypes.RepositoryPrototype;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.services.GitRepositoryService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/repository")
public class RepositoryAPIController extends AbstractController {
    private final GitRepositoryService gitRepositoryService;

    public RepositoryAPIController(GitRepositoryService gitRepositoryService) {
        this.gitRepositoryService = gitRepositoryService;
    }

    @PostMapping
    public RepositoryPrototype create(HttpServletRequest httpServletRequest, @RequestBody CreateRepositoryBody body)
            throws AuthorizationException, RepositoryAlreadyExistsException, InvalidFormatException, RepositoryCreateException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);

        return gitRepositoryService
                .create(userPrototype, body.getName(), body.getDescription(), body.getConfidential());
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class CreateRepositoryBody {
        private String name;
        private String description;
        private Boolean confidential;
    }
}
