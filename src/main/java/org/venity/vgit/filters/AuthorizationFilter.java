package org.venity.vgit.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.eclipse.jgit.transport.RemoteConfig;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.venity.vgit.git.transport.GitHttpServlet;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserRepository;
import org.venity.vgit.services.JWTService;
import org.venity.vgit.services.UserAuthenticationProviderService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Base64;

import static org.venity.vgit.VGitRegex.GIT_URL_PATTERN;
import static org.venity.vgit.configuration.FilterConfiguration.USER_SESSION_KEY;

@Component
public class AuthorizationFilter implements Filter {
    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final UserAuthenticationProviderService userAuthenticationProviderService;

    public AuthorizationFilter(JWTService jwtService, UserRepository userRepository, UserAuthenticationProviderService userAuthenticationProviderService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.userAuthenticationProviderService = userAuthenticationProviderService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        httpServletRequest.setAttribute(USER_SESSION_KEY, null);
        String header = httpServletRequest.getHeader("Authorization");

        if (header != null && header.toLowerCase().startsWith("bearer ")) {
            String jwt = header.substring(7);

            try {
                UserPrototype userPrototype = userRepository.
                        findByLogin(jwtService.extractUsername(jwt))
                        .orElseThrow(() ->
                                new UnavailableException("User not found!"));

                if (jwtService.validateToken(jwt, userPrototype))
                    httpServletRequest.setAttribute(USER_SESSION_KEY, userPrototype);
            } catch (ExpiredJwtException | SignatureException e) {
                // Ignore
            }
        }

        if (header != null && header.toLowerCase().startsWith("basic ")) {
            String base64encoded = header.substring(6);
            String auth = new String(Base64.getDecoder().decode(base64encoded));

            if (auth.indexOf(":") > 0) {
                String username = auth.split(":", 2)[0];
                String password = auth.split(":", 2)[1];

                try {
                    userAuthenticationProviderService.authenticate(new UsernamePasswordAuthenticationToken(username, password));
                    httpServletRequest.setAttribute(USER_SESSION_KEY, userRepository.findByLogin(username).get());
                } catch (AuthenticationException e) {
                    // Ignore
                }
            }
        }

        if (!httpServletRequest.getRequestURI().startsWith(GitHttpServlet.REQUEST_URL)
                && httpServletRequest.getRequestURI().contains(".git")) {

            if (!GIT_URL_PATTERN.matcher(httpServletRequest.getRequestURI()).matches()) {
                chain.doFilter(request, response);

                return;
            }

            try {
                if (httpServletRequest.getQueryString().contains(RemoteConfig.DEFAULT_RECEIVE_PACK)) {
                    chain.doFilter(request, response);

                    return;
                }
            } catch (NullPointerException e) {
                // Ignore
            }

            httpServletRequest.getRequestDispatcher(GitHttpServlet.REQUEST_URL
                    + httpServletRequest.getRequestURI()).forward(request, response);
            return;
        }

        chain.doFilter(request, response);
    }
}
