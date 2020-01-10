package org.venity.vgit.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;
import org.venity.vgit.configuration.ApplicationConfiguration;
import org.venity.vgit.exceptions.*;
import org.venity.vgit.prototypes.ProjectPrototype;
import org.venity.vgit.prototypes.RepositoryPrototype;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.ProjectCrudRepository;
import org.venity.vgit.repositories.RepositoryCrudRepository;
import org.venity.vgit.repositories.UserCrudRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.StreamSupport;

import static org.venity.vgit.VGitRegex.GIT_REPOSITORY_PATTERN;

@Service
public class GitRepositoryService {
    private final RepositoryCrudRepository repositoryCrudRepository;
    private final UserCrudRepository userCrudRepository;
    private final ProjectCrudRepository projectCrudRepository;
    private final ApplicationConfiguration applicationConfiguration;

    public GitRepositoryService(RepositoryCrudRepository repositoryCrudRepository,
                                UserCrudRepository userCrudRepository, ProjectCrudRepository projectCrudRepository,
                                ApplicationConfiguration applicationConfiguration) {
        this.repositoryCrudRepository = repositoryCrudRepository;
        this.userCrudRepository = userCrudRepository;
        this.projectCrudRepository = projectCrudRepository;
        this.applicationConfiguration = applicationConfiguration;
    }

    public RepositoryPrototype create(UserPrototype userPrototype, String name, String project, String description, Boolean confidential)
            throws RepositoryAlreadyExistsException, RepositoryCreateException, InvalidFormatException, ProjectDoesntExistsException, ForbiddenException {
        if (!GIT_REPOSITORY_PATTERN.matcher(name).matches())
            throw new InvalidFormatException();

        ProjectPrototype projectPrototype = projectCrudRepository.findByName(project).orElseThrow(ProjectDoesntExistsException::new);

        if (!canAccess(userPrototype, projectPrototype))
            throw new ForbiddenException();

        var repositoryDirectory = new File(getRepositoryRoot(projectPrototype.getName()), name);

        if (repositoryDirectory.exists())
            throw new RepositoryAlreadyExistsException();

        if (!repositoryDirectory.mkdirs())
            throw new RepositoryCreateException();

        try {
            Git.init().setDirectory(repositoryDirectory).setBare(true).call();
        } catch (GitAPIException e) {
            throw new RepositoryCreateException(e.getMessage());
        }

        var repositoryPrototype = new RepositoryPrototype();
        repositoryPrototype.setName(name);

        if (description != null && !description.isEmpty())
            repositoryPrototype.setDescription(description);

        repositoryPrototype.setMembers(Collections.singleton(userPrototype.getLogin()));
        repositoryPrototype.setConfidential(confidential);
        repositoryPrototype.setProject(project);
        repositoryPrototype.setCreationDate(LocalDateTime.now());
        repositoryPrototype.setLastUpdateDate(repositoryPrototype.getCreationDate());

        userCrudRepository.save(userPrototype);
        repositoryPrototype = repositoryCrudRepository.save(repositoryPrototype);

        var repositories = projectPrototype.getRepositories();
        repositories.add(repositoryPrototype.getId());
        projectPrototype.setRepositories(repositories);

        projectCrudRepository.save(projectPrototype);
        return repositoryPrototype;
    }

    private File getRepositoryRoot(String namespace) {
        return new File(applicationConfiguration.getProperty("storage.repository",
                "./storage/repository"), namespace).getAbsoluteFile();
    }

    public boolean canAccess(UserPrototype userPrototype, ProjectPrototype projectPrototype) {
        return projectPrototype.getMembers().contains(userPrototype.getLogin());
    }

    public boolean canAccess(UserPrototype userPrototype, RepositoryPrototype repositoryPrototype, AccessType type) {
        if (repositoryPrototype == null)
            return false;

        if (type.equals(AccessType.PUSH) && userPrototype == null)
            return false;

        if (type.equals(AccessType.PULL) && !repositoryPrototype.getConfidential())
            return true;

        if (userPrototype == null && repositoryPrototype.getConfidential())
            return false;

        return repositoryPrototype.getMembers().contains(userPrototype.getLogin());
    }

    public GitRepository resolve(String projectWithName) throws RepositoryNotFoundException {
        if (projectWithName.startsWith("/"))
            projectWithName = projectWithName.substring(1);

        if (projectWithName.endsWith(".git"))
            projectWithName = projectWithName.substring(0, projectWithName.length() - 4);

        String name = projectWithName.substring(projectWithName.lastIndexOf("/") + 1);
        String project = projectWithName.substring(0, projectWithName.length() - name.length() - 1);

        return resolve(project, name);
    }

    public GitRepository resolve(String project, String name) throws RepositoryNotFoundException {
        var prototype = repositoryCrudRepository.findByNameAndProject(name, project)
                .orElseThrow(RepositoryNotFoundException::new);

        return resolve(prototype);
    }

    public GitRepository resolve(Integer repositoryId) throws RepositoryNotFoundException {
        var prototype = repositoryCrudRepository.findById(repositoryId)
                .orElseThrow(RepositoryNotFoundException::new);

        return resolve(prototype);
    }

    private GitRepository resolve(RepositoryPrototype repositoryPrototype)
            throws RepositoryNotFoundException {
        try {
            return new GitRepository(repositoryPrototype, Git.open(
                    new File(getRepositoryRoot(repositoryPrototype.getProject()),
                    repositoryPrototype.getName())).getRepository());
        } catch (IOException e) {
            throw new RepositoryNotFoundException();
        }
    }

    public boolean delete(UserPrototype user, GitRepository repository)
            throws ForbiddenException, ProjectDoesntExistsException {
        if (!canAccess(user, repository.getPrototype(), AccessType.PUSH))
            throw new ForbiddenException();

        var projectPrototype = projectCrudRepository.findByName(repository.getPrototype().getProject())
                .orElseThrow(ProjectDoesntExistsException::new);

        if (!canAccess(user, projectPrototype))
            throw new ForbiddenException();

        try {
            FileUtils.deleteDirectory(repository.getRepository().getDirectory());
        } catch (IOException e) {
            return false;
        }

        var repositories = projectPrototype.getRepositories();
        repositories.remove(repository.getPrototype().getId());
        projectPrototype.setRepositories(repositories);

        repositoryCrudRepository.delete(repository.getPrototype());

        return true;
    }

    @SneakyThrows
    public void update(GitRepository gitRepository) {
        gitRepository.getPrototype().setLastUpdateDate(LocalDateTime.now());
        projectCrudRepository.findByName(gitRepository.getPrototype().getProject()).ifPresent(projectPrototype -> {
            projectPrototype.setLastUpdateDate(LocalDateTime.now());
            projectCrudRepository.save(projectPrototype);
        });

        gitRepository.getPrototype().setCommitCount(StreamSupport
                .stream(gitRepository.git()
                        .log()
                        .all()
                        .call()
                        .spliterator(), false)
                .count());

        repositoryCrudRepository.save(gitRepository.getPrototype());
    }

    @Getter
    @AllArgsConstructor
    public static class GitRepository {
        private final RepositoryPrototype prototype;
        private final Repository repository;

        public Git git() {
            return Git.wrap(getRepository());
        }
    }

    public enum AccessType {
        PULL,
        PUSH
    }
}
