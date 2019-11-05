package org.venity.vgit.configuration;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.venity.vgit.git.transport.GitHttpServlet;

@Configuration
public class ServletConfiguration {

    @Bean
    public ServletRegistrationBean<GitHttpServlet> gitHttpServletRegistrationBean() {
        var registrationBean = new ServletRegistrationBean<>(
                new GitHttpServlet());
        registrationBean.addUrlMappings(GitHttpServlet.REQUEST_URL_MAPPING);

        return registrationBean;
    }
}
