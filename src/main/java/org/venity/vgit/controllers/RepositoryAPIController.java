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
            throws AuthorizationException, RepositoryAlreadyExistsException, InvalidFormatException, RepositoryCreateException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);

        return gitRepositoryService
                .create(userPrototype, body.getName(), body.getDescription(), body.getConfidential());
    }

    @GetMapping("/{namespace}/{name}")
    public RepositoryPrototype get(@PathVariable String namespace,
                                   @PathVariable String name) throws RepositoryNotFoundException {
        return gitRepositoryService
                .resolve(namespace, name)
                .getPrototype();
    }

    @DeleteMapping("/{namespace}/{name}")
    public Map<String, Boolean> delete(HttpServletRequest request, @PathVariable String namespace,
                      @PathVariable String name) throws UserDoesntExistsException, RepositoryNotFoundException, ForbiddenException {
        return Collections.singletonMap("status", gitRepositoryService
                .delete(getAuthorization(request).orElseThrow(UserDoesntExistsException::new),
                        namespace, name));
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
