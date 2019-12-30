package org.venity.vgit.filters;

import org.eclipse.jgit.transport.RemoteConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.venity.vgit.git.transport.GitHttpServlet;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserCrudRepository;
import org.venity.vgit.services.JWTService;
import org.venity.vgit.services.UserService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;

import static org.venity.vgit.VGitRegex.GIT_URL_PATTERN;
import static org.venity.vgit.configuration.FilterConfiguration.USER_SESSION_KEY;

@Component
public class AuthorizationFilter implements Filter {
    private final JWTService jwtService;
    private final UserCrudRepository userRepository;
    private final UserService userService;

    public AuthorizationFilter(JWTService jwtService, UserCrudRepository userRepository, UserService userService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest servletRequest = (HttpServletRequest) request;
        String header = servletRequest.getHeader("Authorization");
        servletRequest.setAttribute(USER_SESSION_KEY, null);

        if (header != null && header.startsWith("Bearer ")) {
            String encodedToken = header.substring(7);
            String login = jwtService.extractUsername(encodedToken);

            if (login != null) {
                UserPrototype userPrototype = userRepository
                        .findByLogin(login)
                        .orElseThrow(() -> new UnavailableException("User not found!"));

                if (jwtService.validateToken(encodedToken, userPrototype)) {
                    servletRequest.setAttribute(USER_SESSION_KEY, userPrototype);
                }
            }
        } else if (header != null && header.startsWith("Basic ")) {
            String encodedToken = header.substring(6);
            String decodedToken = new String(Base64.getDecoder().decode(encodedToken));
            String[] tokenParts = decodedToken.split(":", 2);

            if (tokenParts.length == 2) {
                String login = decodedToken.split(":", 2)[0];
                String password = decodedToken.split(":", 2)[1];

                try {
                    UserService.UserAuthenticationToken token =
                            (UserService.UserAuthenticationToken) userService.authenticate(
                                    new UsernamePasswordAuthenticationToken(login, password)
                            );

                    servletRequest.setAttribute(USER_SESSION_KEY, token.getUserPrototype());
                } catch (AuthenticationException ex) {
                    // Ignore
                }
            }
        }

        String requestUri = servletRequest.getRequestURI();

        if (!requestUri.startsWith(GitHttpServlet.REQUEST_URL) && requestUri.contains(".git")) {
            String queryString = servletRequest.getQueryString();

            // Checking that user pushing new changes
            if (!GIT_URL_PATTERN.matcher(requestUri).matches() ||
                    (queryString != null && queryString.contains(RemoteConfig.DEFAULT_RECEIVE_PACK))) {

                chain.doFilter(request, response);
                return;
            }

            servletRequest.getRequestDispatcher(GitHttpServlet.REQUEST_URL + requestUri).forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }
}
