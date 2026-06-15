package com.costuras.reportes.swagger;

import org.springframework.context.annotation.Bean;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

public class SwaggerConfig {
     @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
            .title("MS Reportes")
            .description("Estadísticas de ventas por rango de fechas. Solo ADMIN.")
            .version("1.0.0"))
            .addSecurityItem(new SecurityRequirement()
            .addList("BearerAuth"))
            .components(new Components()
            .addSecuritySchemes("BearerAuth",
                new SecurityScheme()
                .type(SecurityScheme.Type.HTTP).scheme("bearer")
                .bearerFormat("JWT")));
    }

}
