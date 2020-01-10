package org.venity.vgit.git.transport.hooks;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

@Slf4j
public class PushPostReceiveHook extends GitPostReceiveHook {
    private Collection<ReceiveCommand> commands;

    public void run() {
        var template = new RestTemplate();
        var data = new ArrayList<HashMap<String, Object>>();

        commands.iterator().forEachRemaining(receiveCommand -> {
            try {
                var logCommand = Git.wrap(getGitRepository().getRepository()).log();

                if (receiveCommand.getType().equals(ReceiveCommand.Type.UPDATE)) {
                    logCommand = logCommand.addRange(receiveCommand.getOldId(), receiveCommand.getNewId());
                }

                if (receiveCommand.getType().equals(ReceiveCommand.Type.CREATE)) {
                    logCommand = logCommand.add(receiveCommand.getNewId());
                }

                logCommand.call().forEach(revCommit -> {
                    data.add(new HashMap<>() {{
                        put("commit", revCommit.getName());
                        put("author", revCommit.getAuthorIdent());
                        put("message", revCommit.getFullMessage());
                    }});
                });
            } catch (Exception e) {
                log.error("jGit error", e);
            }
        });

        data.sort((o1, o2) -> {
            var pi1 = (PersonIdent) o1.get("author");
            var pi2 = (PersonIdent) o2.get("author");
            return (int) (pi1.getWhen().getTime() - pi2.getWhen().getTime());
        });

        getGitRepository().getPrototype().getHooks().forEach(hookPrototype -> {
            try {
                template.postForLocation(hookPrototype.getUrl(), new HashMap<String, Object>() {{
                    put("type", "PUSH");
                    put("data", data);
                    put("repository", getGitRepository().getPrototype());
                }});
            } catch (Exception e) {
                // Ignore
            }
        });
    }

    @Override
    public void onPostReceive(ReceivePack rp, Collection<ReceiveCommand> commands) {
        this.commands = commands;

        new Thread(this::run).start();
    }
}
