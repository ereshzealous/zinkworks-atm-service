package com.zinkworks.atm.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("zink-works-atm-api")
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(regex("/api.*"))
        .build()
        .apiInfo(apiInfo())
        .securitySchemes(List.of(securityScheme()));
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        "Zink Works - ATM API",
        "Zink Works Java Code Challenge",
        "1.0.0",
        null,
        new Contact("Eresh Gorantla", "https://github.com/ereshzealous", "eresh.zealous@gmail.com"),
        null, null, Collections.emptyList());
  }


  private SecurityScheme securityScheme() {
    return new BasicAuth("basicAuth");
  }
}
