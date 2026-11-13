# 🐾 Vet San Marcos — Backend (Spring Boot)

Backend de la tienda online veterinaria **Vet San Marcos** para la Evaluación Parcial 3 (DSY1104).

## Tecnologías

- Java 17 + Spring Boot 3.2
- Spring Security + JWT
- Spring Data JPA
- H2 (desarrollo) / MySQL (producción)
- springdoc-openapi (Swagger)
- Lombok

## Cómo ejecutar

```bash
# Requiere Java 17+ y Maven (o usa el wrapper)
./mvnw spring-boot:run
# o
mvn spring-boot:run
```

La API queda en `http://localhost:8080`

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **H2 Console**: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:vetsanmarcos`)

## Cuentas de prueba (se crean al arrancar)

| Rol          | Email              | Contraseña    |
|--------------|--------------------|---------------|
| Administrador| admin@duoc.cl      | Admin1234     |
| Vendedor     | vendedor@duoc.cl   | Vendedor1234  |
| Cliente      | cliente@duoc.cl    | Cliente1234   |

## Endpoints principales

| Método | Ruta                        | Auth          | Descripción                  |
|--------|-----------------------------|---------------|------------------------------|
| POST   | /api/v1/auth/login          | Pública       | Login → JWT                  |
| POST   | /api/v1/auth/register       | Pública       | Registro de cliente          |
| GET    | /api/v1/auth/me             | JWT           | Usuario actual               |
| GET    | /api/v1/productos           | Pública       | Listar (filtros + página)    |
| GET    | /api/v1/productos/{id}      | Pública       | Detalle                      |
| POST   | /api/v1/productos           | ADMIN         | Crear                        |
| PUT    | /api/v1/productos/{id}      | ADMIN         | Actualizar                   |
| PATCH  | /api/v1/productos/{id}/stock| ADMIN/VENDEDOR| Actualizar stock             |
| DELETE | /api/v1/productos/{id}      | ADMIN         | Eliminar                     |

> Amplía con controladores de Categorías, Usuarios y Boletas siguiendo el mismo patrón (Repository → Service opcional → Controller + `@PreAuthorize`).

## Configurar MySQL

En `src/main/resources/application.properties` comenta las líneas de H2 y descomenta las de MySQL, ajustando usuario/password.

## Integración con Frontend

El frontend debe:

1. Llamar a `POST /api/v1/auth/login` y guardar el `accessToken`.
2. Enviar en cada request: `Authorization: Bearer <token>`.
3. Usar `GET /api/v1/auth/me` al recargar para restaurar la sesión.
4. Respetar los roles en menús y rutas (ya implementados en el frontend EP2).

## Repositorio

https://github.com/ariecatalan-byte/VetSanMarcos_BACKEND

Equipo: Ariel Catalán · Vinc González — Duoc UC 2026
