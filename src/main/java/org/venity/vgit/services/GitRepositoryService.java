package org.venity.vgit.services;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.springframework.stereotype.Service;
import org.venity.vgit.configuration.ApplicationConfiguration;
import org.venity.vgit.exceptions.InvalidFormatException;
import org.venity.vgit.exceptions.RepositoryAlreadyExistsException;
import org.venity.vgit.exceptions.RepositoryCreateException;
import org.venity.vgit.exceptions.RepositoryNotFoundException;
import org.venity.vgit.prototypes.RepositoryPrototype;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.RepositoryCrudRepository;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

import static org.venity.vgit.VGitRegex.GIT_REPOSITORY_PATTERN;

@Service
public class GitRepositoryService {
    private final RepositoryCrudRepository repositoryCrudRepository;
    private final ApplicationConfiguration applicationConfiguration;

    public GitRepositoryService(RepositoryCrudRepository repositoryCrudRepository, ApplicationConfiguration applicationConfiguration) {
        this.repositoryCrudRepository = repositoryCrudRepository;
        this.applicationConfiguration = applicationConfiguration;
    }

    public RepositoryPrototype create(UserPrototype userPrototype, String name, String description, Boolean confidential)
            throws RepositoryAlreadyExistsException, RepositoryCreateException, InvalidFormatException {
        if (!GIT_REPOSITORY_PATTERN.matcher(name).matches())
            throw new InvalidFormatException();

        var repositoryDirectory = new File(getRepositoryRoot(userPrototype.getLogin()), name);

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

        repositoryPrototype.setNamespace(userPrototype.getLogin());
        repositoryPrototype.setOwner(userPrototype.getId());
        repositoryPrototype.setMaintainers(new HashSet<>());
        repositoryPrototype.setMembers(new HashSet<>());
        repositoryPrototype.setConfidential(confidential);

        return repositoryCrudRepository.save(repositoryPrototype);
    }

    private File getRepositoryRoot(String namespace) {
        return new File(applicationConfiguration.getProperty("storage.repository",
                "./storage/repository"), namespace).getAbsoluteFile();
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

        if (repositoryPrototype.getOwner().equals(userPrototype.getId()))
            return true;

        return repositoryPrototype.getMaintainers().contains(userPrototype.getId())
                || repositoryPrototype.getMembers().contains(userPrototype.getId());
    }

    public GitRepository resolve(String namespaceWithName) throws RepositoryNotFoundException {
        if (namespaceWithName.startsWith("/"))
            namespaceWithName = namespaceWithName.substring(1);

        if (namespaceWithName.endsWith(".git"))
            namespaceWithName = namespaceWithName.substring(0, namespaceWithName.length() - 4);

        String name = namespaceWithName.substring(namespaceWithName.lastIndexOf("/") + 1);
        String namespace = namespaceWithName.substring(0, namespaceWithName.length() - name.length() - 1);

        var prototype = repositoryCrudRepository.findByNameAndNamespace(name, namespace)
                .orElseThrow(RepositoryNotFoundException::new);

        try {
            return new GitRepository(prototype, Git.open(new File(getRepositoryRoot(namespace), name)).getRepository());
        } catch (IOException e) {
            throw new RepositoryNotFoundException();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class GitRepository {
        private final RepositoryPrototype prototype;
        private final Repository repository;
    }

    public enum AccessType {
        PULL,
        PUSH
    }
}
