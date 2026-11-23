# Documentación de APIs — Vet San Marcos

**Curso:** DSY1104 — Desarrollo de Software  
**Versión:** 1.0  
**Fecha:** 22 de noviembre de 2026  

---

## 1. Introducción

Esta documentación describe todos los endpoints de la API REST de Vet San Marcos. La API está versionada bajo `/api/v1` y responde en formato JSON. Está documentada con OpenAPI 3 y disponible en Swagger UI en `http://localhost:8080/swagger-ui.html`.

---

## 2. Autenticación

Todos los endpoints protegidos requieren un token JWT en el encabezado:

```
Authorization: Bearer <access_token>
```

El token de acceso tiene vigencia de **15 minutos**. Cuando expira, el frontend solicita automáticamente uno nuevo mediante `POST /auth/refresh`.

---

## 3. Endpoints de Autenticación (`/api/v1/auth`)

### 3.1 Iniciar sesión

- **Método:** `POST`
- **Ruta:** `/auth/login`
- **Acceso:** Público
- **Solicitud:**
  ```json
  {
    "email": "admin@duoc.cl",
    "password": "Admin1234"
  }
  ```
- **Respuesta exitosa (200 OK):**
  ```json
  {
    "accessToken": "eyJhbGciOi...",
    "refreshToken": "eyJhbGciOi...",
    "usuario": {
      "id": 1,
      "nombre": "Admin",
      "email": "admin@duoc.cl",
      "rol": "ADMIN",
      "permisos": ["panel:acceder", "usuarios:gestionar", ...]
    }
  }
  ```
- **Respuesta error (401 Unauthorized):** Credenciales incorrectas.
- **Respuesta error (423 Locked):** Cuenta bloqueada por intentos fallidos.

### 3.2 Registrar usuario

- **Método:** `POST`
- **Ruta:** `/auth/registro`
- **Acceso:** Público
- **Solicitud:**
  ```json
  {
    "nombre": "Juan Perez",
    "email": "juan@duoc.cl",
    "fechaNacimiento": "1990-01-01",
    "password": "Password123"
  }
  ```
- **Respuesta exitosa (201 Created):** Usuario creado con rol CLIENTE.
- **Respuesta error (400 Bad Request):** Datos inválidos (edad menor a 18, email inválido, contraseña débil).
- **Respuesta error (409 Conflict):** Email ya registrado.

### 3.3 Renovar token

- **Método:** `POST`
- **Ruta:** `/auth/refresh`
- **Acceso:** Público (requiere refresh token válido)
- **Solicitud:**
  ```json
  {
    "refreshToken": "eyJhbGciOi..."
  }
  ```
- **Respuesta exitosa (200 OK):** Nuevo `accessToken` y nuevo `refreshToken` (rotativo).
- **Respuesta error (401 Unauthorized):** Refresh token inválido o expirado.

### 3.4 Cerrar sesión

- **Método:** `POST`
- **Ruta:** `/auth/logout`
- **Acceso:** Autenticado
- **Respuesta exitosa (204 No Content):** Token revocado.

### 3.5 Obtener perfil del usuario autenticado

- **Método:** `GET`
- **Ruta:** `/auth/me`
- **Acceso:** Autenticado
- **Respuesta exitosa (200 OK):** Datos del usuario del token.

### 3.6 Actualizar perfil

- **Método:** `PUT`
- **Ruta:** `/auth/perfil`
- **Acceso:** Autenticado
- **Solicitud:**
  ```json
  {
    "nombre": "Nuevo Nombre",
    "fechaNacimiento": "1990-01-01"
  }
  ```
- **Respuesta exitosa (200 OK):** Perfil actualizado.

---

## 4. Endpoints de Productos (`/api/v1/productos`)

### 4.1 Listar productos

- **Método:** `GET`
- **Ruta:** `/productos`
- **Acceso:** Público
- **Parámetros opcionales:**
  - `categoriaId`: Filtrar por categoría.
  - `busqueda`: Buscar por nombre.
  - `precioMax`: Filtrar por precio máximo.
- **Respuesta exitosa (200 OK):**
  ```json
  [
    {
      "id": 1,
      "nombre": "Antipulgas para perro",
      "descripcion": "Pipeta antipulgas...",
      "precio": 15990.00,
      "stock": 50,
      "categoria": { "id": 1, "nombre": "Salud" }
    }
  ]
  ```

### 4.2 Obtener producto por ID

- **Método:** `GET`
- **Ruta:** `/productos/{id}`
- **Acceso:** Público
- **Respuesta exitosa (200 OK):** Detalle del producto.
- **Respuesta error (404 Not Found):** Producto no encontrado.

### 4.3 Crear producto

- **Método:** `POST`
- **Ruta:** `/productos`
- **Acceso:** ADMIN
- **Solicitud:**
  ```json
  {
    "nombre": "Nuevo producto",
    "descripcion": "Descripción del producto",
    "categoriaId": 1,
    "precio": 9990.00,
    "stock": 100
  }
  ```
- **Respuesta exitosa (201 Created):** Producto creado.
- **Respuesta error (400 Bad Request):** Datos inválidos.

### 4.4 Actualizar producto

- **Método:** `PUT`
- **Ruta:** `/productos/{id}`
- **Acceso:** ADMIN
- **Solicitud:** Mismo formato que crear.
- **Respuesta exitosa (200 OK):** Producto actualizado.

### 4.5 Eliminar producto

- **Método:** `DELETE`
- **Ruta:** `/productos/{id}`
- **Acceso:** ADMIN
- **Respuesta exitosa (204 No Content):** Producto eliminado.

### 4.6 Actualizar stock

- **Método:** `PATCH`
- **Ruta:** `/productos/{id}/stock`
- **Acceso:** ADMIN, VENDEDOR
- **Solicitud:**
  ```json
  {
    "stock": 75
  }
  ```
- **Respuesta exitosa (200 OK):** Stock actualizado.

---

## 5. Endpoints de Categorías (`/api/v1/categorias`)

### 5.1 Listar categorías

- **Método:** `GET`
- **Ruta:** `/categorias`
- **Acceso:** Público
- **Respuesta exitosa (200 OK):**
  ```json
  [
    { "id": 1, "nombre": "Salud", "productosCount": 5 }
  ]
  ```

### 5.2 Crear categoría

- **Método:** `POST`
- **Ruta:** `/categorias`
- **Acceso:** ADMIN
- **Solicitud:**
  ```json
  { "nombre": "Nueva categoría" }
  ```
- **Respuesta exitosa (201 Created):** Categoría creada.

### 5.3 Actualizar categoría

- **Método:** `PUT`
- **Ruta:** `/categorias/{id}`
- **Acceso:** ADMIN
- **Solicitud:**
  ```json
  { "nombre": "Nombre actualizado" }
  ```
- **Respuesta exitosa (200 OK):** Categoría actualizada.

### 5.4 Eliminar categoría

- **Método:** `DELETE`
- **Ruta:** `/categorias/{id}`
- **Acceso:** ADMIN
- **Respuesta exitosa (204 No Content):** Categoría eliminada.
- **Respuesta error (400 Bad Request):** Categoría con productos asociados.

---

## 6. Endpoints de Usuarios (`/api/v1/usuarios`)

### 6.1 Listar usuarios

- **Método:** `GET`
- **Ruta:** `/usuarios`
- **Acceso:** ADMIN
- **Respuesta exitosa (200 OK):** Lista de todos los usuarios (sin contraseña).

### 6.2 Crear usuario

- **Método:** `POST`
- **Ruta:** `/usuarios`
- **Acceso:** ADMIN
- **Solicitud:**
  ```json
  {
    "nombre": "Nuevo Usuario",
    "email": "nuevo@duoc.cl",
    "fechaNacimiento": "1995-05-05",
    "password": "Password123",
    "rol": "CLIENTE"
  }
  ```
- **Respuesta exitosa (201 Created):** Usuario creado.

### 6.3 Actualizar usuario

- **Método:** `PUT`
- **Ruta:** `/usuarios/{id}`
- **Acceso:** ADMIN
- **Solicitud:** Mismo formato que crear (los campos vacíos no se modifican).
- **Respuesta exitosa (200 OK):** Usuario actualizado.

### 6.4 Eliminar usuario

- **Método:** `DELETE`
- **Ruta:** `/usuarios/{id}`
- **Acceso:** ADMIN
- **Respuesta exitosa (204 No Content):** Usuario desactivado (no eliminado físicamente).

### 6.5 Desbloquear usuario

- **Método:** `PATCH`
- **Ruta:** `/usuarios/{id}/desbloquear`
- **Acceso:** ADMIN
- **Respuesta exitosa (200 OK):** Usuario desbloqueado.

---

## 7. Endpoints de Órdenes (`/api/v1/ordenes`)

### 7.1 Crear orden (compra)

- **Método:** `POST`
- **Ruta:** `/ordenes`
- **Acceso:** CLIENTE, VENDEDOR, ADMIN
- **Solicitud:**
  ```json
  {
    "lineas": [
      { "productoId": 1, "cantidad": 2 },
      { "productoId": 3, "cantidad": 1 }
    ]
  }
  ```
- **Respuesta exitosa (201 Created):** Orden registrada con estado "pendiente".
- **Respuesta error (400 Bad Request):** Stock insuficiente.
- **Respuesta error (422 Unprocessable Entity):** Pago rechazado.

### 7.2 Obtener mis órdenes (historial)

- **Método:** `GET`
- **Ruta:** `/ordenes/mis`
- **Acceso:** Autenticado
- **Respuesta exitosa (200 OK):** Solo las órdenes del usuario autenticado.

### 7.3 Obtener orden por ID

- **Método:** `GET`
- **Ruta:** `/ordenes/{id}`
- **Acceso:** ADMIN, VENDEDOR, o dueño de la orden
- **Respuesta exitosa (200 OK):** Detalle de la orden con líneas de compra.
- **Respuesta error (403 Forbidden):** No autorizado.

### 7.4 Listar todas las órdenes

- **Método:** `GET`
- **Ruta:** `/ordenes`
- **Acceso:** ADMIN
- **Parámetros opcionales:**
  - `usuarioId`: Filtrar por usuario.
- **Respuesta exitosa (200 OK):** Todas las órdenes del sistema.

### 7.5 Cambiar estado de orden

- **Método:** `PATCH`
- **Ruta:** `/ordenes/{id}/estado`
- **Acceso:** ADMIN
- **Solicitud:**
  ```json
  { "estado": "PAGADA" }
  ```
- **Estados válidos:** `PENDIENTE`, `PAGADA`, `ENVIADA`, `ENTREGADA`, `CANCELADA`.
- **Nota:** Cancelar una orden devuelve automáticamente el stock.
- **Respuesta exitosa (200 OK):** Estado actualizado.

---

## 8. Endpoints de Reportes (`/api/v1/reportes`)

### 8.1 Obtener resumen de reportes

- **Método:** `GET`
- **Ruta:** `/reportes/resumen`
- **Acceso:** ADMIN
- **Parámetros opcionales:**
  - `periodo`: `semana`, `mes`, `anio`, `todo` (por defecto: `todo`).
- **Respuesta exitosa (200 OK):**
  ```json
  {
    "totalOperaciones": 15,
    "montoTotal": 125985.00,
    "unidadesVendidas": 42,
    "topProductos": [
      { "productoId": 1, "nombre": "Antipulgas", "cantidadVendida": 10 }
    ],
    "ventasPorCategoria": [
      { "categoriaId": 1, "nombre": "Salud", "monto": 50000.00 }
    ]
  }
  ```

---

## 9. Códigos de Estado HTTP

| Código | Significado | Uso en la API |
|---|---|---|
| 200 | OK | Operación exitosa (GET, PUT, PATCH) |
| 201 | Created | Recurso creado (POST) |
| 204 | No Content | Recurso eliminado (DELETE) |
| 400 | Bad Request | Datos inválidos |
| 401 | Unauthorized | Sin autenticación o credenciales incorrectas |
| 403 | Forbidden | Sin permisos para la acción |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Recurso duplicado (email ya registrado) |
| 422 | Unprocessable Entity | Pago rechazado |
| 423 | Locked | Cuenta bloqueada |

---

## 10. Formato de Errores

Todos los errores devuelven un JSON con la siguiente estructura:

```json
{
  "timestamp": "2026-11-22T23:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Datos inválidos",
  "fields": [
    { "field": "email", "message": "Debe ser un email válido" }
  ]
}
```

---

## 11. Swagger UI

La API está documentada con OpenAPI 3 y se puede explorar interactivamente en:

**URL:** `http://localhost:8080/swagger-ui.html`

Allí se pueden probar todos los endpoints directamente desde el navegador con autenticación por Bearer Token.
