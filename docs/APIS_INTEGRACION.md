# Documento de APIs e Integración — Vet San Marcos

**Curso:** DSY1104 — Desarrollo de Software  
**Versión:** 1.0  
**Fecha:** 22 de noviembre de 2026  

---

## 1. Introducción

Este documento describe el modelo de base de datos, los endpoints implementados, la comunicación entre frontend y backend, y las pruebas de integración realizadas en el proyecto Vet San Marcos.

---

## 2. Modelo de Base de Datos

### 2.1 Diagrama Entidad-Relación

El modelo de datos se compone de 5 entidades principales:

```
┌──────────┐       ┌──────────┐       ┌────────────────┐       ┌──────────┐       ┌──────────┐
│  Usuario  │1     N│   Orden   │1     N│ DetalleOrden    │N     1│  Producto  │N     1│ Categoria │
│──────────│──┐ ┌──│──────────│──┐ ┌──│────────────────│──┐ ┌──│──────────│──┐ ┌──│──────────│
│ id       │  │ ││ id         │  │ ││ id               │  │ ││ id       │  │ ││ id       │
│ nombre   │  │ ││ fecha      │  │ ││ cantidad         │  │ ││ nombre   │  │ ││ nombre   │
│ email    │  │ ││ estado     │  │ ││ precioUnitario   │  │ ││ precio   │  │ ││ (FK)     │
│ password │  │ ││ total      │  │ ││ orden (FK)       │  │ ││ stock    │  │ │
│ rol      │  │ ││ usuario(FK)│  │ ││ producto (FK)    │  │ ││ categoria│  │ │
│ activo   │  │ │└────────────┘  │ │└──────────────────┘  │ ││ (FK)     │  │ │
└──────────┘  │ └────────────────┘ └──────────────────────┘ │ └──────────┘  │ └──────────┘
              │                                             │                │
              └─────────────────────────────────────────────┘                │
                                     (relación a través de DetalleOrden)     │
```

### 2.2 Entidades y Campos

#### Usuario

| Campo | Tipo | Descripción |
|---|---|---|
| id | Long (PK, auto) | Identificador único |
| nombre | String | Nombre completo |
| email | String (único) | Correo electrónico |
| password | String | Contraseña hasheada con BCrypt |
| fechaNacimiento | LocalDate | Fecha de nacimiento |
| rol | Enum | ADMIN, VENDEDOR, CLIENTE |
| activo | Boolean | Estado de la cuenta |
| intentosFallidos | Integer | Contador de intentos fallidos de login |

#### Categoria

| Campo | Tipo | Descripción |
|---|---|---|
| id | Long (PK, auto) | Identificador único |
| nombre | String | Nombre de la categoría |

#### Producto

| Campo | Tipo | Descripción |
|---|---|---|
| id | Long (PK, auto) | Identificador único |
| nombre | String | Nombre del producto |
| descripcion | String | Descripción detallada |
| precio | BigDecimal | Precio en CLP |
| stock | Integer | Cantidad disponible |
| categoriaId | Long (FK) | Referencia a Categoria |

#### Orden

| Campo | Tipo | Descripción |
|---|---|---|
| id | Long (PK, auto) | Identificador único |
| fecha | LocalDateTime | Fecha y hora de creación |
| estado | Enum | PENDIENTE, PAGADA, ENVIADA, ENTREGADA, CANCELADA |
| total | BigDecimal | Monto total de la orden |
| usuarioId | Long (FK) | Referencia a Usuario (comprador) |

#### DetalleOrden

| Campo | Tipo | Descripción |
|---|---|---|
| id | Long (PK, auto) | Identificador único |
| cantidad | Integer | Cantidad del producto |
| precioUnitario | BigDecimal | Precio al momento de la compra |
| ordenId | Long (FK) | Referencia a Orden |
| productoId | Long (FK) | Referencia a Producto |

### 2.3 Relaciones

| Relación | Tipo | Descripción |
|---|---|---|
| Usuario → Orden | 1:N | Un usuario puede tener muchas órdenes |
| Orden → DetalleOrden | 1:N | Una orden contiene muchas líneas de detalle |
| DetalleOrden → Producto | N:1 | Una línea de detalle referencia un producto |
| Producto → Categoria | N:1 | Un producto pertenece a una categoría |

### 2.4 Configuración de Base de Datos

- **Base de datos principal:** MySQL (XAMPP)
- **Base de datos alternativa:** H2 (embebida, para desarrollo)
- **URL de conexión:** `jdbc:mysql://localhost:3306/vetsanmarcos?createDatabaseIfNotExist=true`
- **Usuario:** `root`
- **Contraseña:** Vacía (configurable en `.env`)
- **Hibernate DDL:** `update` (crea y actualiza tablas automáticamente)

---

## 3. Endpoints Implementados

### 3.1 Resumen por Controlador

| Controlador | Ruta base | Endpoints |
|---|---|---|
| AuthController | `/api/v1/auth` | login, registro, refresh, logout, me, perfil |
| ProductoController | `/api/v1/productos` | listar, detalle, crear, actualizar, eliminar, stock |
| CategoriaController | `/api/v1/categorias` | listar, crear, actualizar, eliminar |
| UsuarioController | `/api/v1/usuarios` | listar, crear, actualizar, eliminar, desbloquear |
| OrdenController | `/api/v1/ordenes` | crear, mis órdenes, detalle, listar todas, cambiar estado |
| ReporteController | `/api/v1/reportes` | resumen |

### 3.2 Métodos HTTP Utilizados

| Método | Uso | Endpoints |
|---|---|---|
| GET | Consultar datos | Listar productos, categorías, usuarios, órdenes, reportes |
| POST | Crear recursos | Login, registro, crear orden, crear producto, crear categoría, crear usuario |
| PUT | Actualizar completo | Actualizar producto, categoría, usuario, perfil |
| PATCH | Actualizar parcial | Cambiar estado orden, actualizar stock, desbloquear usuario |
| DELETE | Eliminar recursos | Eliminar producto, categoría, usuario |

---

## 4. Comunicación Frontend-Backend

### 4.1 Arquitectura de Comunicación

```
┌─────────────────────────────────────────────────────────────┐
│                        Frontend (React)                     │
│                                                             │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐                │
│  │  Pages   │──▶│  Context │──▶│   API    │                │
│  │ (Vistas) │   │ (Estado) │   │ (Axios)  │                │
│  └──────────┘   └──────────┘   └────┬─────┘                │
│                                     │                       │
└─────────────────────────────────────┼───────────────────────┘
                                      │ HTTP/JSON
                                      │ Authorization: Bearer
                                      ▼
┌─────────────────────────────────────────────────────────────┐
│                      Backend (Spring Boot)                  │
│                                                             │
│  ┌──────────┐   ┌──────────┐   ┌──────────┐                │
│  │Controller│──▶│ Service  │──▶│Repository│                │
│  │ (API)    │   │ (Lógica) │   │ (JPA)    │                │
│  └──────────┘   └──────────┘   └────┬─────┘                │
│                                     │                       │
└─────────────────────────────────────┼───────────────────────┘
                                      │ JDBC
                                      ▼
┌─────────────────────────────────────────────────────────────┐
│                   Base de Datos (MySQL)                     │
│                                                             │
│  Tablas: usuario, categoria, producto, orden, detalle_orden │
└─────────────────────────────────────────────────────────────┘
```

### 4.2 Cliente HTTP del Frontend

El frontend utiliza Axios para comunicarse con el backend. La configuración del cliente está en `src/api/cliente.ts`:

- **URL base:** `http://localhost:8080/api/v1` (desde variable `VITE_API_URL`)
- **Encabezados:** `Content-Type: application/json`
- **Autenticación:** `Authorization: Bearer <access_token>`

### 4.3 Gestión de Tokens

1. **Login:** El backend devuelve el `accessToken` en JSON y establece el refresh token en una cookie `HttpOnly`.
2. **Almacenamiento:** El access token vive en memoria; el refresh token no es accesible para JavaScript y se envía en cookie `SameSite=Strict`, `Path=/api/v1/auth`.
3. **Renovación automática:** Al cargar la aplicación y ante un 401, el frontend solicita un nuevo access token mediante `POST /auth/refresh`; el navegador adjunta la cookie y el backend la rota.
4. **Cierre de sesión:** `POST /auth/logout` revoca el refresh token y expira la cookie. La sesión se restaura tras recargar si el refresh sigue vigente.

Para despliegues HTTPS, configura `REFRESH_COOKIE_SECURE=true`. Mantén frontend y API bajo el mismo sitio para que `SameSite=Strict` permita la cookie.

### 4.4 CORS

El backend configura CORS en `SecurityConfig.java` para permitir solicitudes desde `http://localhost:5173` (frontend en desarrollo). La configuración se lee desde `application.properties`:

```properties
app.cors.allowed-origins=http://localhost:5173
```

Las credenciales CORS se habilitan en `SecurityConfig`; no se requiere una propiedad adicional.

### 4.5 Manejo de Errores

El frontend maneja los errores de comunicación de la siguiente manera:

- **400 Bad Request:** Se muestran los mensajes de validación por campo.
- **401 Unauthorized:** Se intenta renovar el token automáticamente.
- **403 Forbidden:** Se muestra "Acceso denegado".
- **404 Not Found:** Se muestra "Recurso no encontrado".
- **500+ Server Error:** Se muestra un mensaje genérico de error del servidor.

---

## 5. Pruebas de Integración

### 5.1 Pruebas de Backend

Se ejecutaron **21 pruebas de integración** con JUnit 5 y Spring Boot Test. Todas exitosas.

| # | Prueba | Resultado |
|---|---|---|
| 1 | Catálogo público devuelve JSON | ✅ PASÓ |
| 2 | Login correcto entrega tokens y usuario | ✅ PASÓ |
| 3 | Login con credenciales incorrectas da 401 | ✅ PASÓ |
| 4 | Cuenta se bloquea tras 3 intentos fallidos | ✅ PASÓ |
| 5 | Registro público siempre crea cliente | ✅ PASÓ |
| 6 | Registro valida dominio, edad y password | ✅ PASÓ |
| 7 | Endpoint protegido sin token da 401 | ✅ PASÓ |
| 8 | /me devuelve el usuario del token | ✅ PASÓ |
| 9 | Refresh rota el token y el anterior queda inválido | ✅ PASÓ |
| 10 | Logout revoca el refresh token | ✅ PASÓ |
| 11 | Refresh sin cookie es rechazado | ✅ PASÓ |
| 12 | Solo admin lista usuarios | ✅ PASÓ |
| 13 | Vendedor solo modifica stock | ✅ PASÓ |
| 14 | CRUD de productos y categorías como admin | ✅ PASÓ |
| 15 | Gestión y desactivación de usuarios | ✅ PASÓ |
| 16 | Datos inválidos dan 400 con detalle por campo | ✅ PASÓ |
| 17 | Compra se registra, descuenta stock y aparece en historial | ✅ PASÓ |
| 18 | Stock insuficiente y pago rechazado | ✅ PASÓ |
| 19 | Admin cambia estado y cancelar devuelve stock | ✅ PASÓ |
| 20 | Reportes solo admin con datos reales | ✅ PASÓ |
| 21 | Swagger publica endpoints REST | ✅ PASÓ |

**Resultado:** `BUILD SUCCESS — Tests run: 21, Failures: 0, Errors: 0, Skipped: 0`

### 5.2 Pruebas de Comunicación Frontend-Backend

| Prueba | Descripción | Resultado |
|---|---|---|
| 1 | Login con credenciales correctas | Pendiente de verificación manual en navegador |
| 2 | Login con credenciales incorrectas | Pendiente de verificación manual en navegador |
| 3 | Catálogo de productos | Pendiente de verificación manual front-back-DB |
| 4 | Filtros de productos | Pendiente de verificación manual en navegador |
| 5 | Crear orden (compra) | Pendiente de verificación manual front-back-DB |
| 6 | Historial de compras | Pendiente de verificación manual con cuentas distintas |
| 7 | Panel de administración | Pendiente de verificación manual con los roles |
| 8 | CRUD de productos | Pendiente de verificación manual front-back-DB |
| 9 | Cambio de estado de orden | Pendiente de verificación manual front-back-DB |
| 10 | Reportes administrativos | Pendiente de verificación manual con datos de BD |
| 11 | Persistencia de sesión | Pendiente de verificación manual en navegador; el backend renueva con cookie HttpOnly |
| 12 | Cierre de sesión | Pendiente de verificación manual en navegador |
| 13 | Acceso no autorizado por URL directa | Pendiente de verificación manual con los roles |

---

## 6. Integración Visual — Capturas de Pantalla

Esta sección contiene las capturas de pantalla que evidencian la integración entre el frontend y el backend. Cada imagen muestra una operación realizada desde la interfaz de usuario y su resultado, verificando que la comunicación con la API REST funciona correctamente.

> **Nota:** Las imágenes se deben tomar con la aplicación corriendo (`http://localhost:5173/`) y reemplazar los placeholders a continuación.

---

### 6.1 Catálogo de Productos

**Descripción:** El frontend solicita `GET /api/v1/productos` y muestra las tarjetas de productos cargadas desde la base de datos.

![Catálogo de productos](imagenes/catalogo.png)

*Se observan los productos con nombre, categoría, precio y stock, obtenidos desde la API.*

---

### 6.2 Detalle de Producto

**Descripción:** Al hacer clic en un producto, el frontend solicita `GET /api/v1/productos/{id}` y muestra la información completa.

![Detalle de producto](imagenes/detalle-producto.png)

*Se muestra la descripción, precio, categoría y stock disponible del producto seleccionado.*

---

### 6.3 Carrito de Compras

**Descripción:** El carrito muestra los productos agregados con cantidades, subtotales y total calculados por el frontend.

![Carrito de compras](imagenes/carrito.png)

*Los productos en el carrito incluyen botones para modificar cantidad y eliminar, junto con el resumen de la orden.*

---

### 6.4 Confirmación de Compra Exitosa

**Descripción:** Tras confirmar la compra, el frontend envía `POST /api/v1/ordenes` y el backend registra la orden, descuenta el stock y devuelve el resultado.

![Compra exitosa](imagenes/compra-exitosa.png)

*Mensaje de éxito tras registrar la orden correctamente en la base de datos.*

---

### 6.5 Historial de Compras del Cliente

**Descripción:** El frontend solicita `GET /api/v1/ordenes/mis` y muestra únicamente las órdenes del usuario autenticado.

![Historial de compras](imagenes/historial.png)

*Tabla con identificador, fecha, estado y monto total de cada orden del cliente.*

---

### 6.6 Panel de Administración (Dashboard)

**Descripción:** El administrador accede al dashboard que consume `GET /api/v1/reportes/resumen` para mostrar indicadores.

![Dashboard administrativo](imagenes/dashboard.png)

*Tarjetas con total de operaciones, monto vendido, unidades vendidas, gráfico de barras y productos más solicitados.*

---

### 6.7 Gestión de Usuarios

**Descripción:** El administrador gestiona usuarios mediante `GET/POST/PUT/PATCH /api/v1/usuarios`.

![Gestión de usuarios](imagenes/usuarios-admin.png)

*Tabla con todos los usuarios registrados, sus roles y botones para crear, editar y desactivar.*

---

### 6.8 Gestión de Órdenes

**Descripción:** El administrador consulta y actualiza el estado de las órdenes mediante `GET/PATCH /api/v1/ordenes`.

![Gestión de órdenes](imagenes/ordenes-admin.png)

*Tabla con todas las órdenes del sistema y opción para cambiar el estado (pendiente, pagada, enviada, entregada, cancelada).*

---

### 6.9 Mensaje de Error

**Descripción:** Cuando el backend devuelve un error (ej: credenciales incorrectas, stock insuficiente), el frontend lo muestra al usuario.

![Mensaje de error](imagenes/error.png)

*Mensaje de error visible al usuario cuando la operación no puede completarse.*

---

### 6.10 Swagger UI

**Descripción:** La documentación interactiva de la API está disponible en `http://localhost:8080/swagger-ui.html`.

![Swagger UI](imagenes/swagger.png)

*Documentación OpenAPI con todos los endpoints organizados por controlador y métodos HTTP.*

---

## 7. Datos de Prueba

El backend carga datos de prueba automáticamente al iniciar (configurable con `SEED_ENABLED=true` en `.env`):

| Recurso | Cantidad | Detalle |
|---|---|---|
| Usuarios | 7 | 1 admin, 1 vendedor, 5 clientes |
| Categorías | 5 | Salud, Alimentación, Higiene, Juguetes, Accesorios |
| Productos | 12 | Distribuidos entre las 5 categorías |
| Órdenes | 15 | Compras de prueba con diferentes estados |

### Credenciales de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@duoc.cl | Admin1234 |
| Vendedor | vendedor@duoc.cl | Vendedor1234 |
| Cliente | cliente@duoc.cl | Cliente1234 |

---

## 7. Flujo Completo de Compra

1. **Cliente inicia sesión** → `POST /auth/login` → recibe tokens.
2. **Cliente navega catálogo** → `GET /productos?busqueda=&categoriaId=&precioMax=` → obtiene productos.
3. **Cliente agrega al carrito** → Estado local del frontend (React Context).
4. **Cliente confirma compra** → `POST /ordenes` con líneas → backend valida stock, descuenta y registra orden.
5. **Cliente ve historial** → `GET /ordenes/mis` → obtiene sus órdenes.
6. **Admin gestiona orden** → `PATCH /ordenes/{id}/estado` → cambia a "pagada", "enviada", "entregada" o "cancelada".
7. **Admin consulta reportes** → `GET /reportes/resumen?periodo=mes` → obtiene indicadores.

---

## 8. Conclusión

El proyecto Vet San Marcos demuestra una integración completa entre frontend y backend:

- **Backend:** API REST con Spring Boot, autenticación JWT, roles, persistencia con MySQL y documentación Swagger.
- **Frontend:** Aplicación React con TypeScript que consume la API mediante Axios, gestiona en memoria los tokens de sesión y protege rutas por rol.
- **Base de datos:** Modelo relacional con 5 entidades y relaciones que soporta todas las operaciones de negocio.
- **Pruebas:** 21 pruebas de integración automatizadas exitosas; la verificación manual front-back-DB está pendiente.
