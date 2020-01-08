package org.venity.vgit.git.transport;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.transport.ReceiveCommand;
import org.eclipse.jgit.transport.ReceivePack;
import org.springframework.web.client.RestTemplate;
import org.venity.vgit.services.GitRepositoryService;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
public class GitReceivePack extends ReceivePack {

    /**
     * Create a new pack receive for an open repository.
     *
     * @param into the destination repository.
     */
    public GitReceivePack(GitRepositoryService.GitRepository into, GitRepositoryService gitRepositoryService) {
        super(into.getRepository());

        setPostReceiveHook((rp, commands) -> {
            gitRepositoryService.updateLastChangeDate(into.getPrototype());

            new Thread(() -> {
                var template = new RestTemplate();
                var data = new ArrayList<HashMap<String, Object>>();

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

                data.sort((o1, o2) -> {
                    var pi1 = (PersonIdent) o1.get("author");
                    var pi2 = (PersonIdent) o2.get("author");
                    return (int) (pi1.getWhen().getTime() - pi2.getWhen().getTime());
                });

                into.getPrototype().getHooks().forEach(hookPrototype -> {
                    try {
                        template.postForLocation(hookPrototype.getUrl(), new HashMap<String, Object>() {{
                            put("type", "PUSH");
                            put("data", data);
                            put("repository", into.getPrototype());
                        }});
                    } catch (Exception e) {
                        // Ignore
                    }
                });
            }).start();
        });
    }
}
