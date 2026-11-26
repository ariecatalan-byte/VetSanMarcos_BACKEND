# 🐾 Vet San Marcos — Backend (Evaluación Parcial 3, DSY1104)

API REST en **Java 17 + Spring Boot 3.2** con **Spring Security + JWT**, **JPA/Hibernate**, base de datos relacional
(**MySQL**, o **H2 en archivo** para correr sin instalar nada) y documentación **Swagger/OpenAPI**.
Frontend: [`VetSanMarcos`](https://github.com/ariecatalan-byte/VetSanMarcos).

## Cómo ejecutarlo

Requiere JDK 17+ y Maven. Esta carpeta es la raíz del proyecto (donde está `pom.xml`).

```bash
cp .env.example .env     # y edita los valores (el .env NO se sube a GitHub)
```

**Opción A — MySQL** (por defecto): crea el servidor MySQL y pon `DB_USER`/`DB_PASSWORD` en `.env` (la BD `vetsanmarcos` se crea sola).

```bash
mvn spring-boot:run
```

**Opción B — H2 en archivo** (sin MySQL; los datos persisten en `./data/`):

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=h2
```

- API: `http://localhost:8080/api/v1` · Swagger: `http://localhost:8080/swagger-ui.html`
- Si la base está vacía, se cargan **datos de prueba**: 7 usuarios, 5 categorías, 12 productos y 15 compras (para historial y reportes).
- **Persistencia:** detén y vuelve a iniciar la app: lo creado/modificado sigue ahí (los datos de prueba solo se cargan con la BD vacía).

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@duoc.cl | Admin1234 |
| Vendedor | vendedor@duoc.cl | Vendedor1234 |
| Cliente | cliente@duoc.cl | Cliente1234 |

(Otros clientes de prueba: camila.fuentes@, diego.perez@, javiera.munoz@, tomas.araya@ `duoc.cl`, clave `Cliente1234`.)

## Variables de entorno (`.env.example`)

`DB_URL`, `DB_USER`, `DB_PASSWORD`, **`JWT_SECRET`** (obligatoria, ≥ 32 caracteres: `openssl rand -base64 48`), `JWT_ACCESS_MS` (15 min),
`JWT_REFRESH_DAYS` (7), `REFRESH_COOKIE_SECURE` (true en HTTPS), `CORS_ORIGINS`, `SEED_ENABLED`. Ninguna clave de firma tiene valor por defecto.

> ⚠️ El `JWT_SECRET` y la clave anteriores estuvieron publicados en el historial de git. Considéralos comprometidos: usa valores nuevos.

## Estructura

```
com.vetsanmarcos
├── entity/       Usuario, Categoria, Producto, Orden, DetalleOrden, RefreshToken (+ enums Rol, EstadoOrden)
├── repository/   Spring Data JPA
├── service/      Lógica de negocio: Auth, Usuario, Categoria, Producto, Orden, Reporte
├── controller/   Endpoints REST (/api/v1/...) con anotaciones Swagger
├── dto/          Requests (validados con Bean Validation) y responses
├── security/     JwtService, filtro JWT, UserDetailsService, respuestas 401/403 en JSON
├── exception/    ApiException + GlobalExceptionHandler (formato de error único)
└── config/       SecurityConfig (CORS, roles), OpenApiConfig, DataLoader (datos de prueba)
```

Modelo de datos: `usuarios` 1—N `ordenes` 1—N `detalle_orden` N—1 `productos` N—1 `categorias`; `usuarios` 1—N `refresh_tokens`.

## API (resumen)

| Recurso | Endpoints | Acceso |
|---|---|---|
| Auth | `POST /auth/login` · `/auth/register` · `/auth/refresh` · `/auth/logout` · `GET /auth/me` | público (me: autenticado) |
| Productos | `GET /productos[?categoriaId&busqueda&precioMax]` · `GET /productos/{id}` | público |
| | `POST` · `PUT /{id}` · `DELETE /{id}` | ADMIN |
| | `PATCH /{id}/stock` | ADMIN, VENDEDOR |
| Categorías | `GET` público · `POST` · `PUT` · `DELETE` | ADMIN |
| Usuarios | `GET` · `GET /{id}` · `PUT /{id}` · `DELETE /{id}` · `PATCH /{id}/desbloquear` | ADMIN |
| | `POST /usuarios` | ADMIN (cualquier rol) · VENDEDOR (solo cliente) |
| | `PUT /usuarios/me` (perfil propio) | autenticado |
| Órdenes | `POST /ordenes` · `GET /ordenes/mis` · `GET /ordenes/{id}` (dueño o ADMIN) | autenticado |
| | `GET /ordenes[?usuarioId]` · `PATCH /ordenes/{id}/estado` | ADMIN |
| Reportes | `GET /reportes/resumen?periodo=semana\|mes\|anio\|todo` | ADMIN |

Respuestas JSON; códigos: 200, 201 (creado), 204 (eliminado/logout), 400 (validación, con detalle por campo), 401 (sin token válido),
403 (sin permiso), 404, 409 (conflicto: correo repetido, stock insuficiente, categoría con productos…), 423 (cuenta bloqueada).

## Seguridad

- **Contraseñas** con BCrypt. El registro público siempre crea clientes. Cuenta bloqueada tras 3 intentos fallidos.
- **JWT** firmado (HMAC), vigencia 15 min; firma y expiración se validan en cada solicitud; el rol se lee de la BD.
- **Refresh token** aleatorio de 256 bits, guardado como hash SHA-256, **rotativo** (cada uso emite uno nuevo y revoca el anterior); viaja solo en cookie `HttpOnly`, `SameSite=Strict`, limitada a `/api/v1/auth`; reutilizar uno revocado revoca todos los del usuario. `logout` lo revoca y limpia la cookie.
- **Roles** (ADMIN, VENDEDOR, CLIENTE) aplicados con `@PreAuthorize` en cada endpoint, independiente del frontend.
- **Aislamiento:** un cliente solo ve sus órdenes (`GET /ordenes/{id}` ajena → 403).
- **Integridad de compra:** el cliente envía solo `idProducto` y `cantidad`; el servidor calcula precios/total, valida stock con bloqueo de filas y descuenta atómicamente.
- Eliminar un producto/usuario con compras asociadas lo **desactiva** (no rompe el historial).
- El pago es una **simulación académica** (`pagoAprobado`), sin pasarela real.

## Pruebas

```bash
mvn test        # pruebas de integración (MockMvc + H2 en memoria) → ApiIntegrationTest
```

Cubren: catálogo público, login correcto/incorrecto, cookie HttpOnly y renovación de sesión, bloqueo, registro, 401 sin token/token inválido,
roles (403), logout, CRUD, validaciones 400, compra → stock → historial, aislamiento entre usuarios, estados, Swagger y reportes.
La persistencia tras reiniciar puede verificarse con el perfil H2 en archivo; las pruebas automatizadas usan H2 en memoria.
Ver `docs/PRUEBAS_INTEGRACION.md`.

> Repositorio actualizado para entrega.
