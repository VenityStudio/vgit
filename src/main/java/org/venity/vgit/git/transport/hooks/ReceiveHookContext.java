package org.venity.vgit.git.transport.hooks;

import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.venity.vgit.services.GitRepositoryService;

import java.util.Collection;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ReceiveHookContext {
    public GitPostReceiveHook root;

    public ReceiveHookContext(GitRepositoryService gitRepositoryService) {
        addPostReceiveHook(new UpdateChangeReceiveHook(gitRepositoryService));
        addPostReceiveHook(new PushPostReceiveHook());
    }

    public void addPostReceiveHook(GitPostReceiveHook postReceiveHook) {
        if (root == null) {
            root = postReceiveHook;
        } else {
            root = new PostReceiveHookList(root, postReceiveHook);
        }
    }

    public GitPostReceiveHook getRootPostReceiveHook() {
        return root;
    }

    public static class PostReceiveHookList extends GitPostReceiveHook {
        private final GitPostReceiveHook first;
        private final GitPostReceiveHook second;

        public PostReceiveHookList(GitPostReceiveHook first, GitPostReceiveHook second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public void onPostReceive(ReceivePack rp, Collection<ReceiveCommand> commands) {
            first.setGitRepository(getGitRepository());
            second.setGitRepository(getGitRepository());

            first.onPostReceive(rp, commands);
            second.onPostReceive(rp, commands);
        }
    }
}
