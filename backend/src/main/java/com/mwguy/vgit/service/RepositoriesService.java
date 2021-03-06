package com.mwguy.vgit.service;

import com.mwguy.vgit.Git;
import com.mwguy.vgit.configuration.GitConfiguration;
import com.mwguy.vgit.dao.RepositoryDao;
import com.mwguy.vgit.dao.UserDao;
import com.mwguy.vgit.data.GitRepository;
import com.mwguy.vgit.exceptions.GitException;
import com.mwguy.vgit.repositories.RepositoriesRepository;
import com.mwguy.vgit.utils.Authorization;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashSet;

import static com.mwguy.vgit.VGitConstants.*;

@Service
public class RepositoriesService {

    private final RepositoriesRepository repositoriesRepository;
    private final Git git;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRepositoryInput {
        private RepositoryDao.RepositoryPath path;
        private RepositoryDao.AccessPermission accessPermission;
        private String description;
    }

    public RepositoriesService(RepositoriesRepository repositoriesRepository, Git git) {
        this.repositoriesRepository = repositoriesRepository;
        this.git = git;
    }

    public RepositoryDao createNewRepository(CreateRepositoryInput input)
            throws GitException {
        UserDao userDao = Authorization.getCurrentUser(false);
        assert userDao != null;
        if (!input.getPath().getNamespace().equals(userDao.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, NAMESPACE_PERMISSION_DENIED);
        }

        if (input.getPath().getName().length() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, PROVIDED_INVALID_REPOSITORY_NAME);
        }

        if (repositoriesRepository.findByPath_NamespaceAndPath_Name(
                input.getPath().getNamespace(),
                input.getPath().getName()
        ) != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, REPOSITORY_ALREADY_EXISTS);
        }

        Path repositoryPath = GitConfiguration.resolveGitPath(input.path.getNamespace() + "/" + input.getPath().getName());

        git.init()
                .repository(GitRepository.builder().path(repositoryPath).build())
                .bare(true)
                .build()
                .call();

        generateRepositoryHooks(repositoryPath);

        RepositoryDao repositoryDao = new RepositoryDao();
        repositoryDao.setPath(new RepositoryDao.RepositoryPath(
                input.getPath().getName(),
                input.getPath().getNamespace(),
                RepositoryDao.RepositoryPathType.USER));
        repositoryDao.setAccessPermission(input.getAccessPermission());
        repositoryDao.setDescription(input.getDescription());
        repositoryDao.setHooks(new HashSet<>());
        repositoryDao.setMembersIds(Collections.singleton(userDao.getId()));

        return repositoriesRepository.save(repositoryDao);
    }

    public RepositoryDao findRepositoryAndCheckPermissions(
            String namespace,
            String name,
            RepositoryDao.PermissionType type
    ) {
        RepositoryDao repositoryDao = repositoriesRepository.findByPath_NamespaceAndPath_Name(namespace, name);
        if (repositoryDao == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, REPOSITORY_NOT_FOUND);
        }

        if (type == RepositoryDao.PermissionType.HOOK_TRIGGER) {
            if (Authorization.isGitHookTrigger()) {
                return repositoryDao;
            } else {
                throw new BadCredentialsException(PERMISSION_DENIED);
            }
        }

        if (!repositoryDao.checkPermission(type, Authorization.getCurrentUser(true))) {
            throw new BadCredentialsException(UNAUTHORIZED_MESSAGE);
        }

        return repositoryDao;
    }


    @SneakyThrows
    private void generateRepositoryHooks(Path repositoryPath) {
        File pushHookFile = new File(repositoryPath.toFile(), "hooks/post-receive");
        pushHookFile.createNewFile();
        pushHookFile.setWritable(true);
        pushHookFile.setExecutable(true);

        FileWriter writer = new FileWriter(pushHookFile);
        writer.write("#!/bin/bash\n" +
                "curl -H \"Content-Type: text/plain\"" +
                " -H \"Authorization: Bearer ${VGIT_SECRET}\"" +
                " -X POST --data-binary @-" +
                " http://localhost:$VGIT_PORT/$VGIT_REPOSITORY.git/hook/POST_RECEIVE" +
                " > /dev/null 2> /dev/null\n"
        );
        writer.flush();
        writer.close();
    }
}
