package org.venity.vgit.git.transport;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.springframework.stereotype.Component;
import org.venity.vgit.prototypes.UserPrototype;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.venity.vgit.configuration.FilterConfiguration.USER_SESSION_KEY;

@Component
public class GitHttpServlet extends GitServlet {
    public static final String REQUEST_URL = "/" +
            UUID.randomUUID().toString().replace("-", "");

    public static final String REQUEST_URL_MAPPING = REQUEST_URL + "/*";

    public GitHttpServlet() {
        setRepositoryResolver(this::repositoryResolver);
        setReceivePackFactory(this::receivePackFactory);

        // TODO: make git-upload-pack
    }

    private ReceivePack receivePackFactory(HttpServletRequest request, Repository repository) throws ServiceNotAuthorizedException {
        UserPrototype userPrototype = authorizeUser(request)
                .orElseThrow(ServiceNotAuthorizedException::new);

        // TODO: make repository authorization support
        System.err.println("Push by: " + userPrototype.getFullName());

        return new ReceivePack(repository);
    }

    private Repository repositoryResolver(HttpServletRequest request, String s) throws RepositoryNotFoundException {
        // TODO: make repository resolver
        try {
            return Git.open(new File(".")).getRepository();
        } catch (IOException e) {
            e.printStackTrace();

            throw new RepositoryNotFoundException(e.getMessage());
        }
    }

    private Optional<UserPrototype> authorizeUser(HttpServletRequest request) {
        return Optional.ofNullable((UserPrototype) request.getAttribute(USER_SESSION_KEY));
    }
}
