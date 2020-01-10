package org.venity.vgit.git.transport.hooks;

import org.eclipse.jgit.transport.PostReceiveHook;
import org.venity.vgit.services.GitRepositoryService;

public abstract class GitPostReceiveHook implements PostReceiveHook {
    private GitRepositoryService.GitRepository gitRepository;

    public void setGitRepository(GitRepositoryService.GitRepository repository) {
        gitRepository = repository;
    }

    public GitRepositoryService.GitRepository getGitRepository() {
        return gitRepository;
    }
}
