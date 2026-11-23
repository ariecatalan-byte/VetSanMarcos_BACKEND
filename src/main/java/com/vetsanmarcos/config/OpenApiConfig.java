package com.vetsanmarcos.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/** Swagger: botón "Authorize" para pegar el accessToken (Bearer) y probar endpoints protegidos. */
@Configuration
@OpenAPIDefinition(info = @Info(title = "Vet San Marcos API", version = "1.0",
        description = "API REST de la tienda veterinaria Vet San Marcos (DSY1104 - EP3). "
                + "Haz POST /api/v1/auth/login, copia el accessToken y pulsa Authorize."))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {}
