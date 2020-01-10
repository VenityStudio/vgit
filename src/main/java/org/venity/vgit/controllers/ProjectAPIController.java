package org.venity.vgit.controllers;

import org.springframework.web.bind.annotation.*;
import org.venity.vgit.exceptions.*;
import org.venity.vgit.prototypes.ProjectPrototype;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.ProjectCrudRepository;
import org.venity.vgit.services.ProjectService;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/project")
public class ProjectAPIController extends AbstractController {
    private final ProjectService projectService;
    private final ProjectCrudRepository projectCrudRepository;

    public ProjectAPIController(ProjectService projectService, ProjectCrudRepository projectCrudRepository) {
        this.projectService = projectService;
        this.projectCrudRepository = projectCrudRepository;
    }

    @PostMapping
    public ProjectPrototype create(HttpServletRequest httpServletRequest, @RequestBody ProjectService.ProjectCreateBody body)
            throws AuthorizationException, ProjectAlreadyExistsException, InvalidFormatException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);

        return projectService.create(userPrototype, body);
    }

    @GetMapping("/{project}")
    public ProjectPrototype get(@PathVariable String project) throws ProjectDoesntExistsException {
        try {
            return projectCrudRepository.findById(Integer.parseInt(project))
                    .orElseThrow(ProjectDoesntExistsException::new);
        } catch (Exception e) {
            return projectCrudRepository.findByName(project).orElseThrow(ProjectDoesntExistsException::new);
        }
    }

    @PutMapping("/{project}")
    public ProjectPrototype edit(HttpServletRequest httpServletRequest,
                                 @PathVariable String project,
                                 @RequestBody ProjectService.ProjectEditBody body)
            throws AuthorizationException, ProjectDoesntExistsException, ForbiddenException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);

        return projectService.edit(userPrototype, get(project), body);
    }

    @DeleteMapping("/{project}")
    public void delete(HttpServletRequest httpServletRequest, @PathVariable String project)
            throws AuthorizationException, ProjectDoesntExistsException, ForbiddenException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);
        projectService.delete(userPrototype, get(project));
    }
}
