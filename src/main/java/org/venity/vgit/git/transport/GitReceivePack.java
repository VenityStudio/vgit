package org.venity.vgit.git.transport;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.transport.ReceivePack;
import org.venity.vgit.git.transport.hooks.ReceiveHookContext;
import org.venity.vgit.services.GitRepositoryService;

@Slf4j
public class GitReceivePack extends ReceivePack {

    /**
     * Create a new pack receive for an open repository.
     *
     * @param into the destination repository.
     */
    public GitReceivePack(GitRepositoryService.GitRepository into, ReceiveHookContext receiveHookContext) {
        super(into.getRepository());

        setPostReceiveHook((rp, commands) -> {
            var root = receiveHookContext.getRootPostReceiveHook();
            root.setGitRepository(into);
            root.onPostReceive(rp, commands);
        });
    }
}
