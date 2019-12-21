package org.venity.vgit.controllers;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.venity.vgit.exceptions.*;
import org.venity.vgit.prototypes.ProjectPrototype;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.ProjectCrudRepository;
import org.venity.vgit.repositories.UserCrudRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashSet;

@RestController
@RequestMapping("/api/project")
public class ProjectAPIController extends AbstractController {
    private final ProjectCrudRepository projectCrudRepository;
    private final UserCrudRepository userCrudRepository;

    public ProjectAPIController(ProjectCrudRepository projectCrudRepository, UserCrudRepository userCrudRepository) {
        this.projectCrudRepository = projectCrudRepository;
        this.userCrudRepository = userCrudRepository;
    }

    @PostMapping
    public ProjectPrototype create(HttpServletRequest httpServletRequest, @RequestBody ProjectCreateBody body)
            throws AuthorizationException, ProjectAlreadyExistsException, InvalidFormatException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);
        if (projectCrudRepository.existsByName(body.getName()))
            throw new ProjectAlreadyExistsException();

        if (body.getName() == null)
            throw new InvalidFormatException();

        ProjectPrototype projectPrototype = new ProjectPrototype();
        projectPrototype.setName(body.getName());
        projectPrototype.setDescription(body.getDescription());
        projectPrototype.setMembers(Collections.singleton(userPrototype.getLogin()));
        projectPrototype.setRepositories(new HashSet<>());

        projectPrototype = projectCrudRepository.save(projectPrototype);

        var projects = userPrototype.getProjects();
        projects.add(projectPrototype.getId());
        userPrototype.setProjects(projects);

        userCrudRepository.save(userPrototype);
        return projectPrototype;
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

    @DeleteMapping("/{project}")
    public void delete(HttpServletRequest httpServletRequest, @PathVariable String project)
            throws AuthorizationException, ProjectDoesntExistsException, ForbiddenException {
        UserPrototype userPrototype = getAuthorization(httpServletRequest)
                .orElseThrow(AuthorizationException::new);
        ProjectPrototype projectPrototype = get(project);

        if (!projectPrototype.getMembers().contains(userPrototype.getLogin()))
            throw new ForbiddenException();

        projectPrototype.getMembers().forEach(login -> {
            UserPrototype member = userCrudRepository.findByLogin(login).orElse(new UserPrototype());
            var projects = member.getProjects();
            projects.remove(projectPrototype.getId());
            member.setProjects(projects);
            userCrudRepository.save(member);
        });

        projectCrudRepository.delete(projectPrototype);
    }

    @Data
    @AllArgsConstructor
    public static class ProjectCreateBody {
        private String name;
        private String description;
    }
}
