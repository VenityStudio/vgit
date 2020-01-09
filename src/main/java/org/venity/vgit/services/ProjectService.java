package org.venity.vgit.services;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.venity.vgit.exceptions.*;
import org.venity.vgit.prototypes.ProjectPrototype;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.ProjectCrudRepository;
import org.venity.vgit.repositories.UserCrudRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;

@Service
public class ProjectService {
    private final GitRepositoryService gitRepositoryService;
    private final ProjectCrudRepository projectCrudRepository;
    private final UserCrudRepository userCrudRepository;

    public ProjectService(GitRepositoryService gitRepositoryService,
                          ProjectCrudRepository projectCrudRepository,
                          UserCrudRepository userCrudRepository) {
        this.gitRepositoryService = gitRepositoryService;
        this.projectCrudRepository = projectCrudRepository;
        this.userCrudRepository = userCrudRepository;
    }

    public ProjectPrototype create(UserPrototype userPrototype, ProjectCreateBody body)
            throws ProjectAlreadyExistsException, InvalidFormatException {
        if (projectCrudRepository.existsByName(body.getName()))
            throw new ProjectAlreadyExistsException();

        if (body.getName() == null)
            throw new InvalidFormatException();

        ProjectPrototype projectPrototype = new ProjectPrototype();
        projectPrototype.setName(body.getName());
        projectPrototype.setDescription(body.getDescription());
        projectPrototype.setMembers(Collections.singleton(userPrototype.getLogin()));
        projectPrototype.setRepositories(new HashSet<>());
        projectPrototype.setCreationDate(LocalDateTime.now());
        projectPrototype.setLastUpdateDate(projectPrototype.getCreationDate());

        projectPrototype = projectCrudRepository.save(projectPrototype);

        var projects = userPrototype.getProjects();
        projects.add(projectPrototype.getId());
        userPrototype.setProjects(projects);

        userCrudRepository.save(userPrototype);
        return projectPrototype;
    }

    public void delete(UserPrototype userPrototype, ProjectPrototype projectPrototype) throws ForbiddenException {
        if (!projectPrototype.getMembers().contains(userPrototype.getLogin()))
            throw new ForbiddenException();

        projectPrototype.getMembers().forEach(login -> {
            UserPrototype member = userCrudRepository.findByLogin(login).orElse(new UserPrototype());
            var projects = member.getProjects();
            projects.remove(projectPrototype.getId());
            member.setProjects(projects);
            userCrudRepository.save(member);
        });

        projectPrototype.getRepositories().forEach(repositoryId -> {
            try {
                gitRepositoryService.delete(userPrototype, gitRepositoryService.resolve(repositoryId));
            } catch (Exception e) {
                e.printStackTrace();
            }
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
