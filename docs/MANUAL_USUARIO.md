# Manual de Usuario — Vet San Marcos

**Curso:** DSY1104 — Desarrollo de Software  
**Versión:** 1.0  
**Fecha:** 22 de noviembre de 2026

---

## Introducción

### Propósito del documento

Este manual tiene como propósito guiar al usuario en el uso de la aplicación web **Vet San Marcos**, una plataforma de gestión para una tienda de productos veterinarios. Está destinado a tres tipos de usuarios: **clientes** que desean comprar productos, **vendedores** que necesitan consultar el catálogo y las órdenes, y **administradores** que gestionan usuarios, productos, categorías, órdenes y reportes del negocio.

### Descripción del sistema

Vet San Marcos es una aplicación web completa que conecta un frontend desarrollado con React 19 y TypeScript con un backend en Java Spring Boot y base de datos MySQL. Permite a los clientes navegar un catálogo de productos, agregar artículos al carrito, realizar compras y consultar su historial. Los administradores pueden gestionar todo el negocio: usuarios, productos, categorías, estados de las órdenes y reportes estadísticos. El sistema utiliza autenticación JWT con roles y permisos para proteger las rutas y los datos de cada usuario.

### Cómo usar este manual

Este manual está organizado en secciones que siguen el flujo natural de uso de la aplicación:

1. **Requisitos del Sistema:** Qué necesitas para ejecutar la aplicación.
2. **Instalación y Configuración:** Cómo instalar y configurar el entorno.
3. **Comenzando:** Cómo crear una cuenta, iniciar sesión y conocer la interfaz.
4. **Uso del Software:** Descripción detallada de cada función con capturas de pantalla numeradas.

Cada captura de pantalla incluye un número, título, imagen y una nota explicativa para facilitar la comprensión.

---

## Requisitos del Sistema

### Hardware necesario

| Componente | Requisito mínimo |
|---|---|
| **Procesador** | Dual core 1.5 GHz o superior |
| **Memoria RAM** | 4 GB (recomendado 8 GB) |
| **Almacenamiento** | 2 GB de espacio disponible |
| **Conexión a internet** | Para descargar dependencias (Java, Node.js, Maven) |

### Software necesario

| Software | Versión | Descripción |
|---|---|---|
| **Sistema operativo** | Windows 10/11, macOS, Linux | Plataforma de ejecución |
| **Java JDK** | Versión 21 | Runtime del backend Spring Boot |
| **Maven** | Versión 3.10 o superior | Gestión de dependencias del backend |
| **Node.js** | Versión 18 o superior | Runtime del frontend React |
| **NPM** | Versión 10 o superior | Gestor de paquetes del frontend |
| **MySQL / XAMPP** | MySQL 8.x | Base de datos relacional (opcional: H2 embebida) |
| **Navegador web** | Chrome, Firefox, Edge (última versión) | Para acceder a la aplicación |

---

## Instalación y Configuración

### Instrucciones de instalación paso a paso

#### Paso 1: Instalar Java JDK 21

Descarga e instala Eclipse Temurin JDK 21 desde [https://adoptium.net/](https://adoptium.net/) o usa el gestor de paquetes de tu sistema.

**Configura la variable de entorno `JAVA_HOME`:**

```
JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.13.10002-hotspot
```

Agrega `%JAVA_HOME%\bin` al `PATH` del sistema.

#### Paso 2: Instalar Maven

Descarga Apache Maven 3.10 desde [https://archive.apache.org/dist/maven/](https://archive.apache.org/dist/maven/) y descomprime el archivo.

**Configura la variable de entorno `PATH`:**

```
PATH=%PATH%;C:\ruta\a\apache-maven-3.10.0\bin
```

#### Paso 3: Instalar Node.js

Descarga e instala Node.js desde [https://nodejs.org/](https://nodejs.org/). NPM se instala automáticamente.

#### Paso 4: Configurar MySQL (XAMPP)

1. Descarga e instala XAMPP desde [https://www.apachefriends.org/](https://www.apachefriends.org/).
2. Inicia el servicio **MySQL** desde el XAMPP Control Panel.
3. La base de datos `vetsanmarcos` se creará automáticamente al iniciar el backend por primera vez.

#### Paso 5: Clonar los repositorios

```powershell
# Backend
git clone https://github.com/ariecatalan-byte/VetSanMarcos_BACKEND.git
cd VetSanMarcos_BACKEND\VetSanMarcos_BACKEND

# Frontend (en otra carpeta)
git clone https://github.com/ariecatalan-byte/VetSanMarcos.git
cd VetSanMarcos
```

#### Paso 6: Configurar el archivo `.env`

En la carpeta del backend, copia el archivo de ejemplo:

```powershell
copy .env.example .env
```

Edita `.env` con tus credenciales:

```properties
DB_URL=jdbc:mysql://localhost:3306/vetsanmarcos?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USER=root
DB_PASSWORD=
JWT_SECRET=tu_clave_secreta_aqui
SEED_ENABLED=true
```

En la carpeta del frontend, crea `.env`:

```properties
VITE_API_URL=http://localhost:8080/api/v1
```

#### Paso 7: Instalar dependencias del frontend

```powershell
cd C:\ruta\a\VetSanMarcos
npm install
```

---

### Configuración inicial

#### Iniciar el backend

```powershell
cd C:\ruta\a\VetSanMarcos_BACKEND\VetSanMarcos_BACKEND
mvn spring-boot:run
```

El backend se iniciará en `http://localhost:8080` y cargará automáticamente los datos de prueba (7 usuarios, 5 categorías, 12 productos, 15 compras).

#### Iniciar el frontend

```powershell
cd C:\ruta\a\VetSanMarcos
npm run dev
```

El frontend se iniciará en `http://localhost:5173/` y se abrirá automáticamente en el navegador.

---

## Comenzando

### Crear una cuenta / Iniciar sesión

**1. Pantalla de inicio de sesión**

Al abrir `http://localhost:5173/`, se muestra la pantalla de inicio de sesión con dos campos: **Correo electrónico** y **Contraseña**.

> **Imagen 1: Pantalla de inicio de sesión**
>
> ![Inicio de sesión](imagenes/login.png)
>
> *Nota: La pantalla de inicio de sesión muestra los campos de correo electrónico y contraseña, junto con un enlace para registrarse si no se tiene cuenta.*

**2. Iniciar sesión**

Ingresa tus credenciales y haz clic en **"Iniciar sesión"**. Si las credenciales son correctas, serás redirigido automáticamente según tu rol:

- **Cliente:** Página de productos (catálogo).
- **Administrador/Vendedor:** Panel administrativo (Dashboard).

**3. Crear una cuenta nueva**

En la pantalla de inicio de sesión, haz clic en **"¿No tienes cuenta? Regístrate"**. Completa el formulario con:

- **Nombre completo**
- **Correo electrónico** (debe ser válido)
- **Fecha de nacimiento** (debes ser mayor de 18 años)
- **Contraseña** (mínimo 8 caracteres)

Haz clic en **"Registrarse"**. Si los datos son válidos, la cuenta se creará con rol de **cliente**.

> **Imagen 2: Formulario de registro**
>
> ![Registro de usuario](imagenes/registro.png)
>
> *Nota: El formulario de registro solicita nombre, correo, fecha de nacimiento y contraseña. El sistema valida que el usuario sea mayor de 18 años y que la contraseña cumpla los requisitos de seguridad.*

**Credenciales de prueba:**

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | admin@duoc.cl | Admin1234 |
| Vendedor | vendedor@duoc.cl | Vendedor1234 |
| Cliente | cliente@duoc.cl | Cliente1234 |

### Interfaz de usuario inicial

**Pantalla principal del cliente — Catálogo de productos**

Tras iniciar sesión como cliente, se muestra automáticamente el catálogo de productos con una cuadrícula de tarjetas. Cada tarjeta incluye: imagen, nombre, categoría, precio y stock disponible.

> **Imagen 3: Catálogo de productos (interfaz inicial del cliente)**
>
> ![Catálogo de productos](imagenes/catalogo.png)
>
> *Nota: La interfaz principal del cliente muestra una cuadrícula de productos con filtros por categoría, búsqueda por nombre y precio máximo. La barra de navegación incluye enlaces al carrito, el perfil y el botón de cerrar sesión.*

**Pantalla principal del administrador — Dashboard**

Tras iniciar sesión como administrador, se muestra el panel de administración con tarjetas de resumen, gráficos de ventas y acceso a las secciones de gestión.

> **Imagen 4: Dashboard administrativo (interfaz inicial del administrador)**
>
> ![Dashboard](imagenes/dashboard.png)
>
> *Nota: El panel administrativo muestra tarjetas con indicadores clave (total de operaciones, monto vendido, unidades vendidas), un gráfico de barras de ventas por día, los productos más solicitados y barras de progreso por categoría. La barra lateral permite navegar entre usuarios, productos, categorías, órdenes y reportes.*

---

## Uso del Software

### Funciones principales

#### 1. Catálogo de Productos

**Descripción:** El cliente puede navegar el catálogo completo de productos veterinarios. Los productos se muestran en tarjetas con información resumida y se pueden filtrar por categoría, buscar por nombre o filtrar por precio máximo.

**Cómo acceder:** Inicio de sesión como cliente → se abre automáticamente.

> **Imagen 5: Catálogo con filtros aplicados**
>
> ![Catálogo con filtros](imagenes/catalogo-filtros.png)
>
> *Nota: Los filtros permiten seleccionar una categoría, escribir un texto de búsqueda y establecer un precio máximo. Los filtros se combinan entre sí para refinar los resultados.*

---

#### 2. Detalle de Producto

**Descripción:** Al hacer clic en un producto del catálogo, se muestra una vista detallada con nombre, descripción completa, categoría, precio y stock disponible. El cliente puede agregar el producto al carrito desde esta vista.

**Cómo acceder:** Catálogo → Haz clic en un producto.

> **Imagen 6: Detalle de producto**
>
> ![Detalle de producto](imagenes/detalle-producto.png)
>
> *Nota: La vista de detalle muestra toda la información del producto seleccionado, incluyendo descripción, precio, categoría y stock disponible, junto con el botón "Agregar al carrito".*

---

#### 3. Carrito de Compras

**Descripción:** El carrito permite revisar los productos seleccionados, modificar las cantidades, eliminar productos y ver el resumen con subtotales y total antes de confirmar la compra.

**Cómo acceder:** Barra de navegación → Icono del carrito.

> **Imagen 7: Carrito de compras**
>
> ![Carrito de compras](imagenes/carrito.png)
>
> *Nota: El carrito muestra cada producto con botones para aumentar o disminuir la cantidad, un botón para eliminar, y un resumen de la orden con subtotales y total.*

**Acciones disponibles:**

- **Modificar cantidad:** Usa los botones "+" y "-" para ajustar la cantidad de cada producto.
- **Eliminar producto:** Haz clic en el botón de eliminar (icono de basura).
- **Confirmar compra:** Haz clic en **"Confirmar compra"** para proceder al checkout.

---

#### 4. Confirmar Compra (Checkout)

**Descripción:** El checkout muestra un resumen final de la orden. Al confirmar, el sistema valida el stock disponible, descuenta los productos y registra la orden en la base de datos. Si hay stock insuficiente, se muestra un mensaje de error.

**Cómo acceder:** Carrito → "Confirmar compra" → "Confirmar".

> **Imagen 8: Mensaje de compra exitosa**
>
> ![Compra exitosa](imagenes/compra-exitosa.png)
>
> *Nota: Tras confirmar la compra, el sistema muestra un mensaje de éxito indicando que la orden se registró correctamente. El cliente es redirigido al historial de compras.*

---

#### 5. Historial de Compras (Perfil del Cliente)

**Descripción:** El cliente puede consultar todas sus compras anteriores. Se muestra una tabla con identificador, fecha, estado y monto total de cada orden. Al expandir una orden, se ven los productos comprados.

**Cómo acceder:** Barra de navegación → "Mi perfil" → Sección "Mis compras".

> **Imagen 9: Historial de compras del cliente**
>
> ![Historial de compras](imagenes/historial.png)
>
> *Nota: El historial muestra únicamente las órdenes del usuario autenticado con identificador, fecha, estado y monto total. Cada orden se puede expandir para ver los productos y cantidades.*

---

#### 6. Panel de Administración — Dashboard

**Descripción:** El administrador accede a un panel con indicadores del negocio: total de operaciones, monto vendido, unidades vendidas, gráfico de ventas por día, productos más solicitados y ventas por categoría.

**Cómo acceder:** Inicio de sesión como administrador → se abre automáticamente.

> **Imagen 10: Dashboard con indicadores**
>
> ![Dashboard](imagenes/dashboard.png)
>
> *Nota: El dashboard muestra tarjetas de resumen, un gráfico de barras CSS con ventas por día, una tabla de productos más solicitados y barras de progreso por categoría. El botón "Actualizar" refresca los datos desde la base de datos.*

---

#### 7. Reportes Administrativos

**Descripción:** El administrador puede consultar reportes detallados filtrados por periodo: semana, mes, año o todo. Los indicadores se calculan desde la base de datos en tiempo real.

**Cómo acceder:** Dashboard → "Ver reportes" → Seleccionar periodo.

> **Imagen 11: Reportes filtrados por periodo**
>
> ![Reportes](imagenes/reportes.png)
>
> *Nota: Los reportes muestran los mismos indicadores que el dashboard pero filtrados por el periodo seleccionado (semana, mes, año o todo).*

---

#### 8. Gestión de Usuarios

**Descripción:** El administrador puede listar, crear, editar, desactivar y desbloquear usuarios. Se muestra una tabla con todos los usuarios registrados, sus roles y estados.

**Cómo acceder:** Panel admin → "Usuarios".

> **Imagen 12: Gestión de usuarios**
>
> ![Gestión de usuarios](imagenes/usuarios-admin.png)
>
> *Nota: La tabla muestra todos los usuarios con nombre, correo, rol y estado. Los botones permiten crear un nuevo usuario, editar uno existente, desactivar una cuenta o desbloquearla.*

**Acciones disponibles:**

- **Crear usuario:** Haz clic en **"Nuevo usuario"** y completa nombre, correo, fecha de nacimiento, contraseña y rol (ADMIN, VENDEDOR, CLIENTE).
- **Editar usuario:** Haz clic en **"Editar"** en la fila del usuario.
- **Desactivar usuario:** Haz clic en **"Desactivar"** para bloquear la cuenta sin eliminarla.
- **Desbloquear usuario:** Haz clic en **"Desbloquear"** para reactivar una cuenta.

---

#### 9. Gestión de Productos

**Descripción:** El administrador puede listar, crear, editar y eliminar productos. También puede ajustar rápidamente el stock mediante el editor de stock.

**Cómo acceder:** Panel admin → "Productos".

> **Imagen 13: Gestión de productos**
>
> ![Gestión de productos](imagenes/productos-admin.png)
>
> *Nota: La tabla muestra todos los productos con nombre, categoría, precio y stock. Los botones permiten crear, editar y eliminar productos, así como ajustar el stock rápidamente.*

---

#### 10. Gestión de Categorías

**Descripción:** El administrador puede crear, editar y eliminar categorías. No se puede eliminar una categoría si tiene productos asociados.

**Cómo acceder:** Panel admin → "Categorías".

> **Imagen 14: Gestión de categorías**
>
> ![Gestión de categorías](imagenes/categorias-admin.png)
>
> *Nota: La tabla muestra todas las categorías con el nombre y la cantidad de productos asociados. Los botones permiten crear, editar y eliminar categorías.*

---

#### 11. Gestión de Órdenes

**Descripción:** El administrador puede consultar todas las órdenes del sistema, filtrarlas por usuario y cambiar su estado (pendiente, pagada, enviada, entregada, cancelada). Al cancelar una orden, el stock se devuelve automáticamente.

**Cómo acceder:** Panel admin → "Órdenes".

> **Imagen 15: Gestión de órdenes**
>
> ![Gestión de órdenes](imagenes/ordenes-admin.png)
>
> *Nota: La tabla muestra todas las órdenes con ID, cliente, fecha, estado y total. El administrador puede cambiar el estado de cada orden y ver el detalle de los productos.*

---

#### 12. Mensajes de Error

**Descripción:** Cuando el backend devuelve un error (credenciales incorrectas, stock insuficiente, acceso denegado), el frontend muestra un mensaje claro al usuario.

> **Imagen 16: Mensaje de error**
>
> ![Mensaje de error](imagenes/error.png)
>
> *Nota: Los mensajes de error se muestran en la parte superior de la pantalla con un color rojo para alertar al usuario sobre el problema ocurrido.*

---

#### 13. Cerrar Sesión

**Descripción:** El usuario puede cerrar sesión en cualquier momento. Se revoca el token de autenticación, se limpia el estado local y se redirige a la pantalla de inicio de sesión.

**Cómo acceder:** Barra de navegación → "Cerrar sesión".

---

### Navegación por el software

#### Barra de navegación del cliente

La barra superior contiene:

- **Logo / Nombre:** Enlace a la página principal (catálogo).
- **Enlace "Productos":** Catálogo de productos.
- **Icono del carrito:** Carrito de compras con contador de productos.
- **Avatar del usuario:** Menú desplegable con "Mi perfil" y "Cerrar sesión".

> **Imagen 17: Barra de navegación del cliente**
>
> ![Barra de navegación](imagenes/navbar-cliente.png)
>
> *Nota: La barra de navegación muestra el logo, los enlaces principales, el icono del carrito con el contador de productos y el avatar del usuario con menú desplegable.*

#### Barra lateral del administrador

El panel administrativo tiene una barra lateral con:

- **Dashboard:** Panel principal con indicadores.
- **Usuarios:** Gestión de usuarios.
- **Productos:** Gestión de productos.
- **Categorías:** Gestión de categorías.
- **Órdenes:** Gestión de órdenes.
- **Reportes:** Reportes estadísticos.

> **Imagen 18: Barra lateral del panel administrativo**
>
> ![Barra lateral admin](imagenes/sidebar-admin.png)
>
> *Nota: La barra lateral del panel administrativo permite navegar entre las diferentes secciones de gestión: dashboard, usuarios, productos, categorías, órdenes y reportes.*

---

## Seguridad y Sesiones

### Persistencia de Sesión

- La sesión se mantiene activa al recargar la página (`F5` o `Ctrl + R`).
- El sistema renueva automáticamente el token de acceso cuando expira (cada 15 minutos).
- El refresh token tiene vigencia de 7 días.

### Bloqueo de Cuenta

- Tras 3 intentos fallidos de inicio de sesión, la cuenta se bloquea temporalmente.
- Un administrador puede desbloquear la cuenta desde la gestión de usuarios.

### Protección de Rutas

- Sin iniciar sesión, no se puede acceder a ninguna vista privada.
- Un cliente no puede acceder al panel administrativo escribiendo la URL directamente.
- El backend valida los permisos en cada solicitud, incluso si el frontend permite el acceso.

---

## Solución de Problemas Comunes

| Problema | Solución |
|---|---|
| "No se pudo conectar con el servidor" | Verifica que el backend esté corriendo en `http://localhost:8080` |
| "Credenciales incorrectas" | Verifica correo y contraseña; la contraseña es sensible a mayúsculas/minúsculas |
| "Cuenta bloqueada" | Contacta al administrador para desbloquear la cuenta |
| "Stock insuficiente" | Otro usuario compró el producto; actualiza la página para ver el stock actual |
| "Acceso denegado" | Tu rol no tiene permiso para esa acción; contacta al administrador |

---

## Contacto

Para soporte técnico o consultas, contacta al administrador del sistema.
