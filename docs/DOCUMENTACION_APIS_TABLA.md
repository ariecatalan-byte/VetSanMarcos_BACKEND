# Documentación de APIs — Vet San Marcos

**Curso:** DSY1104 — Desarrollo de Software  
**Versión:** 1.0  
**Fecha:** 22 de noviembre de 2026

---

## Información General

| Campo | Valor |
|---|---|
| **URL de la API** | `http://localhost:8080/api/v1` |
| **URL de Swagger** | `http://localhost:8080/swagger-ui.html` |

---

## Descripción General

La API REST de Vet San Marcos gestiona el backend de una aplicación veterinaria que permite a clientes comprar productos, a vendedores gestionar stock y a administradores controlar usuarios, productos, categorías, órdenes y reportes. Está desarrollada con Java y Spring Boot, utiliza base de datos MySQL y autenticación JWT con roles (ADMIN, VENDEDOR, CLIENTE).

---

## Documentación de Endpoints

| Método HTTP | Ruta del Endpoint | Descripción | Datos de entrada | Respuestas | API Pública/Privada | Requiere Autenticación | Roles permitidos | Observaciones |
|---|---|---|---|---|---|---|---|---|
| POST | `/auth/login` | Iniciar sesión y obtener tokens JWT | `{"email": "admin@duoc.cl", "password": "Admin1234"}` | 200: Éxito (tokens), 401: Credenciales incorrectas, 423: Cuenta bloqueada | Pública | No | N/A | Devuelve accessToken (15 min) y refreshToken (7 días) |
| POST | `/auth/registro` | Registrar un nuevo usuario con rol CLIENTE | `{"nombre": "Juan", "email": "juan@duoc.cl", "fechaNacimiento": "1990-01-01", "password": "Password123"}` | 201: Creado, 400: Datos inválidos, 409: Email ya registrado | Pública | No | N/A | Valida edad mínima 18 años y contraseña segura |
| POST | `/auth/refresh` | Renovar token de acceso de forma segura | `{"refreshToken": "eyJhbGciOi..."}` | 200: Nuevos tokens, 401: Refresh token inválido | Pública | Sí (refresh token) | N/A | Token rotativo con hash SHA-256 |
| POST | `/auth/logout` | Cerrar sesión y revocar token | N/A | 204: Token revocado | Privada | Sí | Todos | Invalida el refresh token en la base de datos |
| GET | `/auth/me` | Obtener perfil del usuario autenticado | N/A | 200: Datos del usuario | Privada | Sí | Todos | Devuelve nombre, email, rol y permisos |
| PUT | `/auth/perfil` | Actualizar datos del perfil | `{"nombre": "Nuevo Nombre", "fechaNacimiento": "1990-01-01"}` | 200: Perfil actualizado, 400: Datos inválidos | Privada | Sí | Todos | Solo el usuario puede actualizar su propio perfil |
| GET | `/productos` | Listar todos los productos del catálogo | N/A | 200: Lista de productos, 500: Error del servidor | Pública | No | N/A | Soporta filtros: `?categoriaId&busqueda&precioMax` |
| GET | `/productos/{id}` | Obtener detalle de un producto | N/A | 200: Detalle del producto, 404: No encontrado | Pública | No | N/A | Incluye nombre, descripción, precio, stock y categoría |
| POST | `/productos` | Crear un nuevo producto | `{"nombre": "Producto", "descripcion": "Desc", "categoriaId": 1, "precio": 9990, "stock": 100}` | 201: Creado, 400: Datos inválidos | Privada | Sí | Admin | Requiere categoría válida y precio positivo |
| PUT | `/productos/{id}` | Actualizar información de un producto | `{"nombre": "Actualizado", "precio": 12990, "stock": 50}` | 200: Actualizado, 400: Datos inválidos, 404: No encontrado | Privada | Sí | Admin | Permite actualizar nombre, descripción, precio y stock |
| DELETE | `/productos/{id}` | Eliminar un producto | N/A | 204: Eliminado, 404: No encontrado | Privada | Sí | Admin | Eliminación lógica (desactiva el producto) |
| PATCH | `/productos/{id}/stock` | Actualizar solo el stock | `{"stock": 75}` | 200: Stock actualizado, 404: No encontrado | Privada | Sí | Admin, Vendedor | Útil para ajustes rápidos de inventario |
| GET | `/categorias` | Listar todas las categorías | N/A | 200: Lista de categorías | Pública | No | N/A | Incluye contador de productos por categoría |
| POST | `/categorias` | Crear una nueva categoría | `{"nombre": "Nueva categoría"}` | 201: Creada, 400: Nombre duplicado | Privada | Sí | Admin | El nombre debe ser único |
| PUT | `/categorias/{id}` | Actualizar nombre de una categoría | `{"nombre": "Nombre actualizado"}` | 200: Actualizada, 404: No encontrada | Privada | Sí | Admin | No permite renombrar si hay productos asociados con nombre duplicado |
| DELETE | `/categorias/{id}` | Eliminar una categoría | N/A | 204: Eliminada, 400: Con productos asociados, 404: No encontrada | Privada | Sí | Admin | No se puede eliminar si tiene productos asociados |
| GET | `/usuarios` | Listar todos los usuarios registrados | N/A | 200: Lista de usuarios | Privada | Sí | Admin | No incluye contraseñas ni datos sensibles |
| POST | `/usuarios` | Crear un nuevo usuario con rol asignado | `{"nombre": "Usuario", "email": "u@duoc.cl", "fechaNacimiento": "1995-05-05", "password": "Password123", "rol": "CLIENTE"}` | 201: Creado, 400: Datos inválidos, 409: Email duplicado | Privada | Sí | Admin | Roles disponibles: ADMIN, VENDEDOR, CLIENTE |
| PUT | `/usuarios/{id}` | Actualizar datos de un usuario | `{"nombre": "Nuevo", "rol": "VENDEDOR"}` | 200: Actualizado, 404: No encontrado | Privada | Sí | Admin | Campos vacíos no se modifican |
| DELETE | `/usuarios/{id}` | Desactivar un usuario | N/A | 204: Desactivado, 404: No encontrado | Privada | Sí | Admin | Eliminación lógica, no rompe historial de órdenes |
| PATCH | `/usuarios/{id}/desbloquear` | Desbloquear una cuenta bloqueada | N/A | 200: Desbloqueado, 404: No encontrado | Privada | Sí | Admin | Resetea el contador de intentos fallidos |
| POST | `/ordenes` | Crear una nueva orden (compra) | `{"lineas": [{"productoId": 1, "cantidad": 2}, {"productoId": 3, "cantidad": 1}]}` | 201: Orden creada, 400: Stock insuficiente, 422: Pago rechazado | Privada | Sí | Cliente, Vendedor, Admin | Descuenta stock atómicamente y simula pago |
| GET | `/ordenes/mis` | Obtener historial de compras del usuario | N/A | 200: Lista de órdenes propias | Privada | Sí | Todos | Cada usuario solo ve sus propias órdenes |
| GET | `/ordenes/{id}` | Obtener detalle de una orden | N/A | 200: Detalle con líneas, 403: No autorizado, 404: No encontrada | Privada | Sí | Admin, Vendedor, Dueño | Incluye productos, cantidades, precios y estado |
| GET | `/ordenes` | Listar todas las órdenes del sistema | N/A | 200: Todas las órdenes | Privada | Sí | Admin | Soporta filtro: `?usuarioId` |
| PATCH | `/ordenes/{id}/estado` | Cambiar el estado de una orden | `{"estado": "PAGADA"}` | 200: Estado actualizado, 400: Estado inválido, 404: No encontrada | Privada | Sí | Admin | Estados: PENDIENTE, PAGADA, ENVIADA, ENTREGADA, CANCELADA. Cancelar devuelve stock |
| GET | `/reportes/resumen` | Obtener resumen estadístico del negocio | N/A | 200: Resumen con indicadores | Privada | Sí | Admin | Soporta filtro: `?periodo=semana\|mes\|anio\|todo` |

---

## Códigos de Estado HTTP

| Código | Significado | Uso en la API |
|---|---|---|
| 200 | OK | Operación exitosa (GET, PUT, PATCH) |
| 201 | Created | Recurso creado correctamente (POST) |
| 204 | No Content | Recurso eliminado o desactivado (DELETE) |
| 400 | Bad Request | Datos de entrada inválidos |
| 401 | Unauthorized | Sin autenticación o credenciales incorrectas |
| 403 | Forbidden | Sin permisos para la acción solicitada |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Recurso duplicado (ej: email ya registrado) |
| 422 | Unprocessable Entity | Pago rechazado |
| 423 | Locked | Cuenta bloqueada por intentos fallidos |

---

## Formato de Errores

Todos los errores devuelven un JSON con la siguiente estructura:

```json
{
  "timestamp": "2026-11-22T23:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Datos inválidos",
  "fields": [
    {"field": "email", "message": "Debe ser un email válido"}
  ]
}
```

---

## Integración con el Frontend

El frontend (React 19) se comunica con la API mediante `fetch` configurado en `http://localhost:8080/api/v1`. Cada solicitud protegida incluye el token JWT en el encabezado `Authorization: Bearer <token>`. Cuando el token expira, el frontend renueva automáticamente la sesión mediante `POST /auth/refresh`.

La comunicación está habilitada con CORS configurado en el backend para aceptar solicitudes desde `http://localhost:5173`.

---

## Swagger UI

La API está documentada con OpenAPI 3 y se puede explorar interactivamente en:

**URL:** `http://localhost:8080/swagger-ui.html`

Allí se pueden probar todos los endpoints directamente desde el navegador, visualizar las estructuras de solicitud y respuesta, y verificar los códigos de estado HTTP.
