# Manual de Usuario — Vet San Marcos

**Curso:** DSY1104 — Desarrollo de Software  
**Versión:** 1.0  
**Fecha:** 22 de noviembre de 2026  

---

## 1. Introducción

Vet San Marcos es una aplicación web para la gestión de una tienda de productos veterinarios. Este manual explica cómo usar la aplicación desde las diferentes perspectivas: **cliente**, **vendedor** y **administrador**.

---

## 2. Acceso a la Aplicación

### 2.1 Iniciar sesión

1. Abre el navegador y ve a `http://localhost:5173/`.
2. Se mostrará la pantalla de **Inicio de sesión** con dos campos: **Correo electrónico** y **Contraseña**.
3. Ingresa tus credenciales y haz clic en **"Iniciar sesión"**.
4. Si las credenciales son correctas, serás redirigido automáticamente según tu rol:
   - **Cliente:** Página de productos (catálogo).
   - **Administrador/Vendedor:** Panel administrativo (Dashboard).

### 2.2 Crear una cuenta

1. En la pantalla de inicio de sesión, haz clic en **"¿No tienes cuenta? Regístrate"**.
2. Completa el formulario con:
   - **Nombre completo**
   - **Correo electrónico** (debe ser válido)
   - **Fecha de nacimiento** (debes ser mayor de 18 años)
   - **Contraseña** (mínimo 8 caracteres)
3. Haz clic en **"Registrarse"**.
4. Si los datos son válidos, la cuenta se creará con rol de **cliente** y podrás iniciar sesión inmediatamente.

### 2.3 Credenciales de prueba

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@duoc.cl | Admin1234 |
| Vendedor | vendedor@duoc.cl | Vendedor1234 |
| Cliente | cliente@duoc.cl | Cliente1234 |

---

## 3. Funcionalidades del Cliente

### 3.1 Catálogo de Productos

**Acceso:** Inicio de sesión como cliente → se abre automáticamente.

- Se muestra una cuadrícula con tarjetas de productos que incluyen: imagen, nombre, categoría, precio y stock disponible.
- **Filtros disponibles:**
  - **Categoría:** Selecciona una categoría del menú desplegable para filtrar.
  - **Búsqueda:** Escribe un texto para buscar por nombre de producto.
  - **Precio máximo:** Ingresa un monto para filtrar productos por debajo de ese precio.
- Los filtros se combinan entre sí.

### 3.2 Detalle de Producto

**Acceso:** Catálogo → Haz clic en un producto.

- Se muestra información completa del producto: nombre, descripción, categoría, precio, stock disponible.
- Botón **"Agregar al carrito"** para añadir el producto.

### 3.3 Carrito de Compras

**Acceso:** Barra de navegación → Icono del carrito.

- Se listan los productos agregados con su cantidad, precio unitario y subtotal.
- **Acciones disponibles:**
  - **Modificar cantidad:** Usa los botones "+" y "-" para ajustar la cantidad.
  - **Eliminar producto:** Haz clic en el botón de eliminar (icono de basura).
- En la sección **Resumen de la orden** se muestran:
  - Subtotal por producto
  - Total de la compra
  - Botón **"Confirmar compra"** para proceder al checkout.

### 3.4 Confirmar Compra (Checkout)

**Acceso:** Carrito → "Confirmar compra".

- Se muestra un resumen final de la orden con todos los productos y el total.
- Haz clic en **"Confirmar"** para registrar la compra.
- El sistema valida el stock disponible y descuenta los productos.
- Si hay stock insuficiente, se muestra un mensaje de error.
- Si la compra es exitosa, se redirige al historial de compras.

### 3.5 Historial de Compras (Perfil)

**Acceso:** Barra de navegación → "Mi perfil".

- Se muestra la información del usuario: nombre, correo, rol.
- Sección **"Mis compras"** con una tabla que lista todas las órdenes del usuario:
  - Identificador de la orden
  - Fecha y hora
  - Estado (pendiente, pagada, enviada, entregada, cancelada)
  - Monto total
- Haz clic en **"Ver detalle"** para expandir y ver los productos de cada orden.
- El historial persiste al cerrar sesión y al cambiar de dispositivo (datos en base de datos).

### 3.6 Cerrar Sesión

**Acceso:** Barra de navegación → "Cerrar sesión".

- Se revoca el token de autenticación y se limpia el estado local.
- Se redirige a la pantalla de inicio de sesión.

---

## 4. Funcionalidades del Administrador

### 4.1 Dashboard (Panel Principal)

**Acceso:** Inicio de sesión como administrador → se abre automáticamente.

- **Tarjetas de resumen:**
  - Total de operaciones realizadas
  - Monto total vendido
  - Unidades vendidas
- **Gráfico de barras:** Ventas por día.
- **Productos más solicitados:** Tabla con los productos más vendidos.
- **Barras de progreso:** Ventas por categoría.
- Botón **"Actualizar"** para refrescar los datos.
- Botón **"Ver reportes"** para acceder a reportes detallados con filtro por periodo.

### 4.2 Reportes Detallados

**Acceso:** Dashboard → "Ver reportes".

- Selecciona un **periodo**: Semana, Mes, Año o Todo.
- Se muestran los mismos indicadores que el Dashboard, filtrados por el periodo seleccionado.
- Los datos se calculan desde la base de datos en tiempo real.

### 4.3 Gestión de Usuarios

**Acceso:** Panel admin → "Usuarios".

- **Listar usuarios:** Se muestra una tabla con todos los usuarios registrados (nombre, correo, rol, estado).
- **Crear usuario:** Haz clic en **"Nuevo usuario"** y completa el formulario:
  - Nombre completo
  - Correo electrónico
  - Fecha de nacimiento
  - Contraseña
  - Rol (ADMIN, VENDEDOR, CLIENTE)
- **Editar usuario:** Haz clic en **"Editar"** en la fila del usuario.
- **Desactivar usuario:** Haz clic en **"Desactivar"** para bloquear la cuenta sin eliminarla (se preserva el historial).
- **Desbloquear usuario:** Haz clic en **"Desbloquear"** para reactivar una cuenta desactivada.

### 4.4 Gestión de Productos

**Acceso:** Panel admin → "Productos".

- **Listar productos:** Tabla con todos los productos (nombre, categoría, precio, stock).
- **Crear producto:** Haz clic en **"Nuevo producto"** y completa:
  - Nombre
  - Descripción
  - Categoría (seleccionar de la lista)
  - Precio
  - Stock
- **Editar producto:** Haz clic en **"Editar"** para modificar cualquier campo.
- **Eliminar producto:** Haz clic en **"Eliminar"** para borrar el producto.
- **Editor de stock:** Permite ajustar rápidamente el stock de un producto.

### 4.5 Gestión de Categorías

**Acceso:** Panel admin → "Categorías".

- **Listar categorías:** Tabla con todas las categorías y cantidad de productos asociados.
- **Crear categoría:** Haz clic en **"Nueva categoría"** e ingresa el nombre.
- **Editar categoría:** Haz clic en **"Editar"** para renombrar.
- **Eliminar categoría:** Haz clic en **"Eliminar"** (solo si no tiene productos asociados).

### 4.6 Gestión de Órdenes

**Acceso:** Panel admin → "Órdenes".

- **Listar órdenes:** Tabla con todas las órdenes del sistema (ID, cliente, fecha, estado, total).
- **Filtrar por usuario:** Selecciona un usuario del menú desplegable para ver solo sus órdenes.
- **Cambiar estado:** Haz clic en el menú de estado y selecciona el nuevo estado:
  - Pendiente → Pagada → Enviada → Entregada
  - Cualquier estado → Cancelada (devuelve el stock automáticamente)
- **Ver detalle:** Haz clic en **"Ver detalle"** para ver los productos de la orden.

---

## 5. Funcionalidades del Vendedor

El vendedor tiene acceso limitado:

- **Ver catálogo de productos** (solo lectura).
- **Ver detalle de productos** (solo lectura).
- **Ver órdenes** y su detalle (solo lectura).
- **No puede** crear, editar ni eliminar usuarios, productos, categorías ni órdenes.

---

## 6. Seguridad y Sesiones

### 6.1 Persistencia de Sesión

- La sesión se mantiene activa al recargar la página (`F5` o `Ctrl + R`).
- El sistema renueva automáticamente el token de acceso cuando expira (cada 15 minutos).
- El refresh token tiene vigencia de 7 días.

### 6.2 Bloqueo de Cuenta

- Tras 3 intentos fallidos de inicio de sesión, la cuenta se bloquea temporalmente.
- Un administrador puede desbloquear la cuenta desde la gestión de usuarios.

### 6.3 Protección de Rutas

- Sin iniciar sesión, no se puede acceder a ninguna vista privada.
- Un cliente no puede acceder al panel administrativo escribiendo la URL directamente.
- El backend valida los permisos en cada solicitud, incluso si el frontend permite el acceso.

---

## 7. Solución de Problemas Comunes

| Problema | Solución |
|---|---|
| "No se pudo conectar con el servidor" | Verifica que el backend esté corriendo en `http://localhost:8080` |
| "Credenciales incorrectas" | Verifica correo y contraseña; la contraseña es sensible a mayúsculas/minúsculas |
| "Cuenta bloqueada" | Contacta al administrador para desbloquear la cuenta |
| "Stock insuficiente" | Otro usuario compró el producto; actualiza la página para ver el stock actual |
| "Acceso denegado" | Tu rol no tiene permiso para esa acción; contacta al administrador |

---

## 8. Contacto

Para soporte técnico o consultas, contacta al administrador del sistema.
