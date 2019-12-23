package org.venity.vgit.git.transport;

import org.eclipse.jgit.errors.RepositoryNotFoundException;
import org.eclipse.jgit.http.server.GitServlet;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.ReceivePack;
import org.eclipse.jgit.transport.resolver.ServiceNotAuthorizedException;
import org.springframework.stereotype.Component;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.services.GitRepositoryService;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;

import static org.venity.vgit.configuration.FilterConfiguration.USER_SESSION_KEY;

@Component
public class GitHttpServlet extends GitServlet {
    public static final String REQUEST_URL = "/" +
            UUID.randomUUID().toString().replace("-", "");

    public static final String REQUEST_URL_MAPPING = REQUEST_URL + "/*";
    public static final String REPOSITORY_PROTOTYPE_KEY = "repository-key";
    private final GitRepositoryService gitRepositoryService;

    public GitHttpServlet(GitRepositoryService gitRepositoryService) {
        this.gitRepositoryService = gitRepositoryService;
        setRepositoryResolver(this::repositoryResolver);
        setReceivePackFactory(this::receivePackFactory);
    }

    private ReceivePack receivePackFactory(HttpServletRequest request, Repository repository) throws ServiceNotAuthorizedException {
        UserPrototype userPrototype = authorizeUser(request)
                .orElseThrow(ServiceNotAuthorizedException::new);
        var gitRepository = (GitRepositoryService.GitRepository) request.getSession(true).getAttribute(REPOSITORY_PROTOTYPE_KEY);

        if (gitRepositoryService.canAccess(userPrototype, gitRepository.getPrototype(), GitRepositoryService.AccessType.PUSH))
            return new GitReceivePack(gitRepository);

        throw new ServiceNotAuthorizedException();
    }

    private Repository repositoryResolver(HttpServletRequest request, String s) throws RepositoryNotFoundException, ServiceNotAuthorizedException {
        var userPrototype = authorizeUser(request);

        try {
            var repository = gitRepositoryService.resolve(s);

            if (gitRepositoryService.canAccess(userPrototype.orElse(null), repository.getPrototype(), GitRepositoryService.AccessType.PULL)) {
                request.getSession(true).setAttribute(REPOSITORY_PROTOTYPE_KEY, repository);
                return repository.getRepository();
            } else throw new ServiceNotAuthorizedException();
        } catch (org.venity.vgit.exceptions.RepositoryNotFoundException e) {
            throw new RepositoryNotFoundException(s);
        }
    }

    private Optional<UserPrototype> authorizeUser(HttpServletRequest request) {
        return Optional.ofNullable((UserPrototype) request.getAttribute(USER_SESSION_KEY));
    }
}
