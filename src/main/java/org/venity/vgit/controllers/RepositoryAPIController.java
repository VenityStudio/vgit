package org.venity.vgit.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.venity.vgit.exceptions.*;
import org.venity.vgit.prototypes.RepositoryPrototype;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.services.GitRepositoryService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/repository")
public class RepositoryAPIController extends AbstractController {
    private final GitRepositoryService gitRepositoryService;

    public RepositoryAPIController(GitRepositoryService gitRepositoryService) {
        this.gitRepositoryService = gitRepositoryService;
    }

    @PostMapping
    public RepositoryPrototype create(HttpServletRequest httpServletRequest, @RequestBody CreateRepositoryBody body)
            throws AuthorizationException, RepositoryAlreadyExistsException, InvalidFormatException, RepositoryCreateException,
            ForbiddenException, ProjectDoesntExistsException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);

        return gitRepositoryService
                .create(userPrototype, body.getName(), body.getProject(), body.getDescription(), body.getConfidential());
    }

    @GetMapping("/{repositoryId}")
    public RepositoryPrototype get(@PathVariable String repositoryId) throws RepositoryNotFoundException {
        return gitRepositoryService
                .resolve(Integer.parseInt(repositoryId))
                .getPrototype();
    }

    @DeleteMapping("/{repositoryId}")
    public Map<String, Boolean> delete(HttpServletRequest request, @PathVariable String repositoryId)
            throws UserDoesntExistsException, RepositoryNotFoundException, ForbiddenException, ProjectDoesntExistsException {
        return Collections.singletonMap("status", gitRepositoryService
                .delete(getAuthorization(request).orElseThrow(UserDoesntExistsException::new), gitRepositoryService.resolve(Integer.parseInt(repositoryId))));
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class CreateRepositoryBody {
        private String name;
        private String description;
        private String project;
        private Boolean confidential;
    }
}
