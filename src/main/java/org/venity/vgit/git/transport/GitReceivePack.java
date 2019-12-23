package org.venity.vgit.git.transport;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.springframework.web.client.RestTemplate;
import org.venity.vgit.prototypes.HookPrototype;
import org.venity.vgit.services.GitRepositoryService;

import java.util.HashMap;
import java.util.HashSet;

@Slf4j
public class GitReceivePack extends ReceivePack {

    /**
     * Create a new pack receive for an open repository.
     *
     * @param into the destination repository.
     */
    public GitReceivePack(GitRepositoryService.GitRepository into) {
        super(into.getRepository());

        setPostReceiveHook((rp, commands) -> into.getPrototype().getHooks().forEach(hookPrototype -> {
            new Thread(() -> {
                if (hookPrototype.getType().equals(HookPrototype.HookType.PUSH)) {
                    var template = new RestTemplate();
                    var data = new HashSet<HashMap<String, Object>>();

                    commands.iterator().forEachRemaining(receiveCommand -> {
                        try {
                            var logCommand = Git.wrap(into.getRepository()).log();

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

                    try {
                        template.postForLocation(hookPrototype.getUrl(), new HashMap<String, Object>() {{
                            put("type", "PUSH");
                            put("data", data);
                            put("repository", into.getPrototype());
                        }});
                    } catch (Exception e) {
                        // Ignore
                    }
                }
            }).start();
        }));
    }
}
