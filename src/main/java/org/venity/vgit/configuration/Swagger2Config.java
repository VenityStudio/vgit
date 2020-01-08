package org.venity.vgit.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.ServletContext;

@Configuration
@EnableSwagger2
public class Swagger2Config {
    private final ServletContext servletContext;

    public Swagger2Config(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .pathProvider(new RelativePathProvider(servletContext))
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.venity.vgit.controllers"))
                .paths(PathSelectors.regex("/.*"))
                .build().apiInfo(apiEndPointsInfo());
    }

    private ApiInfo apiEndPointsInfo() {
        return new ApiInfoBuilder()
                .title("VGit REST API")
                .description("VGit REST API")
                .contact(new Contact("Maxim Tarasov", "https://venity.site/", "yousan4ik@gmail.com"))
                .license("MPL-2.0")
                .licenseUrl("https://www.mozilla.org/en-US/MPL/2.0/")
                .version("1.0.0-SNAPSHOT")
                .build();
    }
}
