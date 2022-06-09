package com.tiger.analytics.security;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.tiger.analytics")
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder().group("Case Study").packagesToScan("com.tiger.analytics").build();
    }

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Tiger Analytics")
                .description("Case study to get and save retailer products")
                .version("1.0")
                .contact(new Contact().name("Pushparani").email("pushpakokila@gmail.com")));
    }
}
