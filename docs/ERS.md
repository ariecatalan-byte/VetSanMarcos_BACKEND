# Documento ERS — Vet San Marcos

**Curso:** DSY1104 — Desarrollo de Software  
**Evaluación:** Parcial 3  
**Estudiante:** Ariel Catalan  
**Fecha:** 22 de noviembre de 2026  

---

## 1. Introducción

Vet San Marcos es una aplicación web para la gestión de una tienda de productos veterinarios. Permite a los clientes navegar un catálogo, agregar productos al carrito y realizar compras, mientras que los administradores pueden gestionar usuarios, productos, categorías, órdenes y consultar reportes. El proyecto se compone de un **frontend** en React 19 con TypeScript y un **backend** en Java con Spring Boot, conectados mediante una API REST y una base de datos MySQL.

---

## 2. Requerimientos

### 2.1 Requerimientos Funcionales

| ID | Descripción | Prioridad |
|---|---|---|
| RF-01 | El sistema permite registrar nuevos usuarios con rol de cliente | Alta |
| RF-02 | El sistema permite iniciar sesión con credenciales almacenadas en la base de datos | Alta |
| RF-03 | El sistema genera tokens JWT para autenticación y los renueva de forma segura | Alta |
| RF-04 | El sistema restringe el acceso a endpoints según el rol del usuario (ADMIN, VENDEDOR, CLIENTE) | Alta |
| RF-05 | El cliente puede visualizar el catálogo de productos con filtros por categoría, nombre y precio máximo | Alta |
| RF-06 | El cliente puede consultar el detalle de cada producto | Media |
| RF-07 | El cliente puede agregar, eliminar y modificar cantidades en el carrito de compras | Alta |
| RF-08 | El cliente puede confirmar una compra, registrando la orden en la base de datos | Alta |
| RF-09 | El sistema valida el stock disponible antes de registrar una compra y descuenta atómicamente | Alta |
| RF-10 | El cliente puede consultar su historial de compras desde su perfil | Alta |
| RF-11 | El administrador puede listar, crear, editar y desactivar usuarios | Alta |
| RF-12 | El administrador puede gestionar productos (CRUD completo) | Alta |
| RF-13 | El administrador puede gestionar categorías (CRUD completo) | Alta |
| RF-14 | El administrador puede consultar y actualizar el estado de las órdenes | Alta |
| RF-15 | El administrador puede consultar reportes con totales de ventas, unidades vendidas y productos más solicitados | Alta |
| RF-16 | El vendedor puede ver productos, detalle de productos, órdenes y detalle de órdenes | Media |
| RF-17 | El sistema bloquea una cuenta tras tres intentos fallidos de inicio de sesión | Media |
| RF-18 | El sistema mantiene la sesión activa al recargar la página mediante refresh token | Alta |
| RF-19 | El sistema limpia el estado de autenticación al cerrar sesión | Alta |
| RF-20 | El sistema impide que un usuario consulte información privada de otro usuario | Alta |

### 2.2 Requerimientos No Funcionales

| ID | Descripción |
|---|---|
| RNF-01 | Las contraseñas se almacenan con BCrypt (hash seguro) |
| RNF-02 | Los tokens JWT se firman con HMAC y tienen vigencia limitada (15 minutos access, 7 días refresh) |
| RNF-03 | La API REST responde en formato JSON con códigos de estado HTTP apropiados |
| RNF-04 | El frontend es responsivo y se adapta a teléfonos, tablets y computadores |
| RNF-05 | La base de datos se crea automáticamente si no existe (`createDatabaseIfNotExist=true`) |
| RNF-06 | Los endpoints están documentados con Swagger/OpenAPI |
| RNF-07 | La comunicación frontend-backend está protegida con CORS configurado |
| RNF-08 | Las credenciales sensibles (JWT_SECRET, DB_PASSWORD) no se publican en GitHub |

---

## 3. Herramientas y Tecnologías

### 3.1 Frontend

| Tecnología | Versión | Propósito |
|---|---|---|
| React | 19.1.0 | Biblioteca principal de interfaz |
| TypeScript | 5.9.3 | Tipado estático |
| Vite | 5.4.21 | Herramienta de desarrollo y build |
| Bootstrap | 5.3.8 | Framework CSS responsivo |
| React Router DOM | 6.x | Navegación entre vistas |
| Axios | 1.x | Cliente HTTP para API |

### 3.2 Backend

| Tecnología | Versión | Propósito |
|---|---|---|
| Java | 21 (Eclipse Temurin) | Lenguaje de programación |
| Spring Boot | 3.x | Framework principal |
| Spring Security | 6.x | Autenticación y autorización |
| Spring Data JPA | 3.x | Persistencia con Hibernate |
| JWT (jjwt) | 0.12.x | Tokens de autenticación |
| MySQL Connector | 8.x | Conector de base de datos |
| H2 | 2.x | Base de datos embebida para desarrollo |
| SpringDoc OpenAPI | 2.x | Documentación Swagger |
| Maven | 3.10.0 | Gestión de dependencias y build |
| JUnit 5 | 5.x | Pruebas unitarias e integración |

### 3.3 Base de Datos

| Tecnología | Propósito |
|---|---|
| MySQL (XAMPP) | Base de datos relacional principal |
| H2 | Base de datos embebida alternativa para desarrollo |

### 3.4 Entorno de Desarrollo

| Herramienta | Propósito |
|---|---|
| IntelliJ IDEA / VS Code | IDE de desarrollo |
| Git + GitHub | Control de versiones y alojamiento |
| PowerShell | Terminal de Windows |
| Postman / Swagger UI | Pruebas de API |

---

## 4. Propuestas del Proyecto

### 4.1 Arquitectura

El proyecto sigue una arquitectura **cliente-servidor** con separación clara entre frontend y backend:

- **Frontend:** Aplicación SPA (Single Page Application) en React que se comunica con el backend mediante HTTP/JSON.
- **Backend:** API REST en Spring Boot que expone endpoints versionados (`/api/v1`) y gestiona la lógica de negocio.
- **Base de datos:** MySQL como almacenamiento persistente, con JPA/Hibernate como ORM.

### 4.2 Modelo de Datos

El modelo de datos se compone de 5 entidades principales con relaciones:

- **Usuario** (1-N) → **Orden**
- **Orden** (1-N) → **DetalleOrden**
- **DetalleOrden** (N-1) → **Producto**
- **Producto** (N-1) → **Categoria**

### 4.3 Seguridad

- Autenticación basada en JWT con tokens de acceso (15 min) y refresh (7 días).
- Refresh token rotativo con hash SHA-256 almacenado en base de datos.
- Roles: ADMIN, VENDEDOR, CLIENTE con permisos granulares.
- Bloqueo de cuenta tras 3 intentos fallidos.
- Contraseñas con BCrypt.

### 4.4 Pruebas

- 21 pruebas de integración que cubren login, JWT, roles, CRUD, compras, historial, reportes y seguridad.
- 100% de pruebas exitosas documentadas en `docs/PRUEBAS_INTEGRACION.md`.

---

## 5. Repositorios

| Componente | URL |
|---|---|
| Frontend | https://github.com/ariecatalan-byte/VetSanMarcos |
| Backend | https://github.com/ariecatalan-byte/VetSanMarcos_BACKEND |
