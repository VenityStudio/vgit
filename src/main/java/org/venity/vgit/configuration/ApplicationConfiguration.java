package org.venity.vgit.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

@Configuration
@PropertySources({
        @PropertySource(value = "file:configuration.properties", ignoreResourceNotFound = true),
        @PropertySource(value = "file:configuration.dev.properties", ignoreResourceNotFound = true)
})
public class ApplicationConfiguration {
    private final Environment environment;

    public ApplicationConfiguration(Environment environment) {
        this.environment = environment;
    }

    public String getProperty(String key) {
        return environment.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return environment.getProperty(key, defaultValue);
    }

    public boolean hasProperty(String key) {
        return environment.containsProperty(key);
    }
}
