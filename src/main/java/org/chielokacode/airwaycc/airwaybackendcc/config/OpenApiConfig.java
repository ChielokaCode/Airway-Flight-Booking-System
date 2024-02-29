package org.chielokacode.airwaycc.airwaybackendcc.model;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@SecurityScheme(
        name = "Bearer Authentication",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "Bearer"
)
@OpenAPIDefinition(
        info = @Info(description = "Airway App",
                title = "Airway RESTful API",
                version = "v1")
)
public class OpenApiConfig {

}
