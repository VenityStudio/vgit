package org.venity.vgit.configuration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.venity.vgit.git.transport.GitHttpServlet;
import org.venity.vgit.git.transport.hooks.ReceiveHookContext;
import org.venity.vgit.services.GitRepositoryService;

@Configuration
public class ServletConfiguration {

    @Bean
    public ServletRegistrationBean<GitHttpServlet> gitHttpServletRegistrationBean(
            GitRepositoryService gitRepositoryService,
            ReceiveHookContext context) {
        var registrationBean = new ServletRegistrationBean<>(
                new GitHttpServlet(gitRepositoryService, context));
        registrationBean.addUrlMappings(GitHttpServlet.REQUEST_URL_MAPPING);

        return registrationBean;
    }
}
