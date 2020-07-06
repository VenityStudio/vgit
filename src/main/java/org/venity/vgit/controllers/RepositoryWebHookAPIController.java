package org.venity.vgit.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import org.springframework.web.bind.annotation.*;
import org.venity.vgit.exceptions.*;
import org.venity.vgit.prototypes.HookPrototype;
import org.venity.vgit.prototypes.RepositoryPrototype;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.RepositoryCrudRepository;
import org.venity.vgit.services.GitRepositoryService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@RestController
@RequestMapping("/api/repository/webhook")
public class RepositoryWebHookAPIController extends AbstractController {
    private final RepositoryCrudRepository repositoryCrudRepository;
    private final GitRepositoryService gitRepositoryService;

    public RepositoryWebHookAPIController(RepositoryCrudRepository repositoryCrudRepository,
                                          GitRepositoryService gitRepositoryService) {
        this.repositoryCrudRepository = repositoryCrudRepository;
        this.gitRepositoryService = gitRepositoryService;
    }

    @PostMapping("/{repository}")
    public void add(HttpServletRequest request, @RequestBody AddWebHookBody body, @PathVariable String repository)
            throws AuthorizationException, RepositoryNotFoundException, ForbiddenException,
            HookAlreadyExistsException {
        UserPrototype userPrototype = getAuthorization(request)
                .orElseThrow(AuthorizationException::new);
        RepositoryPrototype repositoryPrototype = repositoryCrudRepository
                .findById(repository).orElseThrow(RepositoryNotFoundException::new);

        if (!gitRepositoryService.canAccess(
                userPrototype, repositoryPrototype, GitRepositoryService.AccessType.PUSH))
            throw new ForbiddenException();

        for (HookPrototype hookPrototype : repositoryPrototype.getHooks()) {
            if (hookPrototype.getName().equals(body.getName()))
                throw new HookAlreadyExistsException();
        }

        var hook = new HookPrototype();
        hook.setName(body.getName());
        hook.setUrl(body.getUrl());
        hook.setType(HookPrototype.HookType.valueOf(body.getType().toUpperCase()));

        var hooks = repositoryPrototype.getHooks();
        hooks.add(hook);
        repositoryPrototype.setHooks(hooks);

        repositoryCrudRepository.save(repositoryPrototype);
    }

    @GetMapping("/{repository}")
    public Collection<HookPrototype> get(HttpServletRequest request, @PathVariable String repository)
            throws AuthorizationException, RepositoryNotFoundException, ForbiddenException {
        UserPrototype userPrototype = getAuthorization(request)
                .orElseThrow(AuthorizationException::new);
        RepositoryPrototype repositoryPrototype = repositoryCrudRepository
                .findById(repository).orElseThrow(RepositoryNotFoundException::new);

        if (!gitRepositoryService.canAccess(
                userPrototype, repositoryPrototype, GitRepositoryService.AccessType.PUSH))
            throw new ForbiddenException();

        return repositoryPrototype.getHooks();
    }

    @DeleteMapping("/{repository}")
    public void delete(HttpServletRequest request, @PathVariable String repository, @RequestBody DeleteWebHookBody body)
            throws AuthorizationException, RepositoryNotFoundException, ForbiddenException, HookDoesntExistsException {
        UserPrototype userPrototype = getAuthorization(request)
                .orElseThrow(AuthorizationException::new);
        RepositoryPrototype repositoryPrototype = repositoryCrudRepository
                .findById(repository).orElseThrow(RepositoryNotFoundException::new);

        if (!gitRepositoryService.canAccess(
                userPrototype, repositoryPrototype, GitRepositoryService.AccessType.PUSH))
            throw new ForbiddenException();

        for (HookPrototype hookPrototype : repositoryPrototype.getHooks()) {
            if (hookPrototype.getName().equals(body.getName())) {
                var hooks = repositoryPrototype.getHooks();
                hooks.remove(hookPrototype);
                repositoryPrototype.setHooks(hooks);
                repositoryCrudRepository.save(repositoryPrototype);
                return;
            }
        }

        throw new HookDoesntExistsException();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddWebHookBody {
        private String name;
        private String type;
        @URL private String url;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeleteWebHookBody {
        private String name;
    }
}
