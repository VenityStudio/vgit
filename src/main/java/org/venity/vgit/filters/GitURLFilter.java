package org.venity.vgit.filters;

import org.venity.vgit.git.transport.GitHttpServlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.venity.vgit.VGitRegex.GIT_URL_PATTERN;

public class GitURLFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (!GIT_URL_PATTERN.matcher(httpServletRequest.getRequestURI()).matches()) {
            chain.doFilter(request, response);
            return;
        }

        if (!httpServletRequest.getRequestURI().startsWith(GitHttpServlet.REQUEST_URL)
                && httpServletRequest.getRequestURI().contains(".git")) {
            httpServletRequest.getRequestDispatcher(GitHttpServlet.REQUEST_URL
                    + httpServletRequest.getRequestURI()).forward(request, response);

            return;
        }

        chain.doFilter(request, response);
    }
}
