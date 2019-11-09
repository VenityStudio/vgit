package org.venity.vgit.configuration;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    private final ApplicationConfiguration configuration;

    public DataSourceConfiguration(ApplicationConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public DataSource dataSource() {
        var dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("com.mysql.cj.jdbc.Driver")
                .url(configuration.getProperty("mysql.url",
                        "jdbc:mysql://"
                                + configuration.getProperty("mysql.host", "localhost")
                                + ":"
                                + configuration.getProperty("mysql.port", "3306")
                                + "/"
                                + configuration.getProperty("mysql.database", "vgit")
                                + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"))
                .username(configuration.getProperty("mysql.username", "root"))
                .password(configuration.getProperty("mysql.password", ""));
        return dataSourceBuilder.build();
    }
}
