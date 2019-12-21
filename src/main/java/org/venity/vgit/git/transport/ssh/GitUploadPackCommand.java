package org.venity.vgit.git.transport.ssh;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.command.AbstractCommandSupport;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.UploadPack;
import org.venity.vgit.exceptions.RepositoryNotFoundException;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.services.GitRepositoryService;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;

@Slf4j
public class GitUploadPackCommand extends AbstractCommandSupport {
    private final GitRepositoryService gitRepositoryService;
    private final UserPrototype userPrototype;

    public GitUploadPackCommand(String command, GitRepositoryService gitRepositoryService, UserPrototype userPrototype) {
        super(command, null);
        this.gitRepositoryService = gitRepositoryService;
        this.userPrototype = userPrototype;
    }

    @Override
    public void run() {
        String path = getCommand()
                .substring(RemoteConfig.DEFAULT_UPLOAD_PACK.length())
                .replaceAll("'", "")
                .trim();
        GitRepositoryService.GitRepository repository;

        try {
             repository = gitRepositoryService.resolve(path);
        } catch (RepositoryNotFoundException e) {
            onExit(-1, "Repository not found");
            return;
        }

        if (!gitRepositoryService.canAccess(userPrototype,
                repository.getPrototype(),
                GitRepositoryService.AccessType.PULL)) {
            onExit(-1, "Repository not found");
            return;
        }

        UploadPack uploadPack = new UploadPack(repository.getRepository());
        String gitProtocol = getEnvironment().getEnv().get("GIT_PROTOCOL");
        if (gitProtocol != null) {
            uploadPack
                    .setExtraParameters(Collections.singleton(gitProtocol));
        }
        try {
            uploadPack.upload(getInputStream(), getOutputStream(),
                    getErrorStream());
            onExit(0);
        } catch (IOException e) {
            log.warn(MessageFormat.format("Could not run {0}", getCommand()), e);
            onExit(-1, e.toString());
        }
    }
}
