package org.venity.vgit.git.transport.ssh;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.server.command.AbstractCommandSupport;
import org.eclipse.jgit.transport.RemoteConfig;
import org.venity.vgit.exceptions.RepositoryNotFoundException;
import org.venity.vgit.git.transport.GitReceivePack;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.services.GitRepositoryService;

import java.io.IOException;
import java.text.MessageFormat;

@Slf4j
public class GitReceivePackCommand extends AbstractCommandSupport {
    private final GitRepositoryService gitRepositoryService;
    private final UserPrototype userPrototype;

    public GitReceivePackCommand(String command,
                                 GitRepositoryService gitRepositoryService, UserPrototype userPrototype) {
        super(command, null);
        this.gitRepositoryService = gitRepositoryService;
        this.userPrototype = userPrototype;
    }

    @Override
    public void run() {
        String path = getCommand()
                .substring(RemoteConfig.DEFAULT_RECEIVE_PACK.length())
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
                GitRepositoryService.AccessType.PUSH)) {
            onExit(-1, "Repository not found");
            return;
        }

        try {
            new GitReceivePack(repository, gitRepositoryService).receive(getInputStream(),
                    getOutputStream(), getErrorStream());
            onExit(0);
        } catch (IOException e) {
            log.warn(MessageFormat.format("Could not run {0}", getCommand()), e);
            onExit(-1, e.toString());
        }
    }
}
