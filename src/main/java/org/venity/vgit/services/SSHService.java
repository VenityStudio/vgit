package org.venity.vgit.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.common.AttributeRepository;
import org.apache.sshd.common.config.keys.KeyUtils;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.shell.UnknownCommand;
import org.eclipse.jgit.transport.RemoteConfig;
import org.springframework.stereotype.Service;
import org.venity.vgit.configuration.ApplicationConfiguration;
import org.venity.vgit.exceptions.AuthorizationException;
import org.venity.vgit.git.transport.hooks.ReceiveHookContext;
import org.venity.vgit.git.transport.ssh.GitReceivePackCommand;
import org.venity.vgit.git.transport.ssh.GitUploadPackCommand;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserCrudRepository;

import java.io.IOException;
import java.nio.file.Path;
import java.security.PublicKey;
import java.util.Map;

@Service
@Slf4j
public class SSHService {
    private static final AttributeRepository.AttributeKey<UserPrototype> sessionUserKey
            = new AttributeRepository.AttributeKey<>();
    private final GitRepositoryService gitRepositoryService;
    private final UserCrudRepository userCrudRepository;
    private final ReceiveHookContext context;
    private final SshServer server;

    public SSHService(ApplicationConfiguration configuration,
                      GitRepositoryService gitRepositoryService,
                      UserCrudRepository userCrudRepository,
                      ReceiveHookContext context)
            throws IOException {
        this.gitRepositoryService = gitRepositoryService;
        this.userCrudRepository = userCrudRepository;
        this.context = context;

        if (configuration.hasProperty("ssh.enabled")
                && configuration.getProperty("ssh.enabled").toLowerCase().equals("false")) {
            server = null;
            return;
        }

        server = SshServer.setUpDefaultServer();
        server.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(
                Path.of(configuration.getProperty("ssh.server.key.file", "secret_ssh.key"))));
        server.setPort(Integer.parseInt(configuration.getProperty("ssh.server.port", "8022")));
        configureAuthentication();
        configureGitCommands();
        server.start();

        log.info("Starting SSH server at port {}", server.getPort());
    }

    private void configureAuthentication() {
        server.setPasswordAuthenticator(null);
        server.setKeyboardInteractiveAuthenticator(null);
        server.setPublickeyAuthenticator((username, key, session) -> {
            UserPrototype userPrototype;
            try {
                userPrototype = userCrudRepository.findByLogin(username)
                        .orElseThrow(AuthorizationException::new);
            } catch (AuthorizationException e) {
                return false;
            }

            boolean authenticate = false;

            for (Map.Entry<String, PublicKey> entry : userPrototype.getPublicKeys().entrySet()) {
                PublicKey userKey = entry.getValue();
                if (KeyUtils.compareKeys(key, userKey)) {
                    session.setAttribute(sessionUserKey, userPrototype);
                    authenticate = true;
                }
            }

            return authenticate;
        });
    }

    private void configureGitCommands() {
        server.setCommandFactory((channel, command) -> {
            if (command.startsWith(RemoteConfig.DEFAULT_UPLOAD_PACK))
                return new GitUploadPackCommand(command, gitRepositoryService, channel.getSession().getAttribute(sessionUserKey));
            else if (command.startsWith(RemoteConfig.DEFAULT_RECEIVE_PACK))
                return new GitReceivePackCommand(command, gitRepositoryService, channel.getSession().getAttribute(sessionUserKey), context);
            else return new UnknownCommand(command);
        });
    }
}
