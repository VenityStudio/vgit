package org.venity.vgit.git.transport.hooks;

import lombok.SneakyThrows;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.venity.vgit.services.GitRepositoryService;

import java.util.Collection;

public class UpdateChangeReceiveHook extends GitPostReceiveHook {
    private final GitRepositoryService gitRepositoryService;

    public UpdateChangeReceiveHook(GitRepositoryService gitRepositoryService) {
        this.gitRepositoryService = gitRepositoryService;
    }

    @Override
    @SneakyThrows
    public void onPostReceive(ReceivePack rp, Collection<ReceiveCommand> commands) {
        gitRepositoryService.update(getGitRepository());
    }
}
