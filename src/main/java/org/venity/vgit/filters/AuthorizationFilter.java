package org.venity.vgit.filters;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.stereotype.Component;
import org.venity.vgit.prototypes.UserPrototype;
import org.venity.vgit.repositories.UserRepository;
import org.venity.vgit.services.JWTService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.venity.vgit.configuration.FilterConfiguration.USER_SESSION_KEY;

@Component
public class AuthorizationFilter implements Filter {
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public AuthorizationFilter(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        httpServletRequest.setAttribute(USER_SESSION_KEY, null);
        String header = httpServletRequest.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
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

        chain.doFilter(request, response);
    }
}
