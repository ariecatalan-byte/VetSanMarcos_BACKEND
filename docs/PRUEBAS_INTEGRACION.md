# Pruebas de integración del backend

Clase: `src/test/java/com/vetsanmarcos/ApiIntegrationTest.java`. Usa el contexto completo de Spring, la configuración real de seguridad (JWT y roles), MockMvc y H2 en memoria con datos de prueba. Ejecutar desde la raíz del backend con `mvn test`.

| # | Método de prueba | Verifica |
|---|---|---|
| 1 | `catalogoEsPublicoYDevuelveJson` | Catálogo y categorías públicos; recurso inexistente → 404 |
| 2 | `loginCorrectoEntregaAccessTokenYCookieHttpOnly` | Access token en JSON, refresh token ausente del JSON y cookie HttpOnly/SameSite/Path |
| 3 | `loginConCredencialesIncorrectasDa401` | Credenciales erróneas o usuario inexistente → 401 |
| 4 | `cuentaSeBloqueaTrasTresIntentosFallidos` | Bloqueo después de intentos fallidos |
| 5 | `registroPublicoSiempreCreaCliente` | El registro público fuerza rol cliente y rechaza correo duplicado |
| 6 | `registroValidaDominioEdadYPassword` | Validaciones de dominio, edad y contraseña |
| 7 | `endpointProtegidoSinTokenOTokenInvalidoDa401` | Endpoint protegido rechaza ausencia de token o token inválido |
| 8 | `meDevuelveElUsuarioDelToken` | `/auth/me` devuelve al usuario autenticado |
| 9 | `refreshRotaElTokenYElAnteriorQuedaInvalido` | Renovación por cookie, rotación y revocación por reutilización |
| 10 | `logoutRevocaElRefreshToken` | Logout revoca el token y expira la cookie |
| 11 | `refreshSinCookieDa401` | Renovación sin cookie → 401 |
| 12 | `soloAdminListaUsuarios` | Acceso al listado según rol |
| 13 | `vendedorSoloModificaStockYSoloCreaClientes` | Permisos de vendedor y restricción para crear roles privilegiados |
| 14 | `crudDeProductosYCategoriasComoAdmin` | Operaciones de productos y categorías y validaciones |
| 15 | `adminGestionaUsuariosYDesactivaQuienesTienenCompras` | CRUD de usuarios y desactivación para conservar compras |
| 16 | `datosInvalidosDan400ConDetallePorCampo` | Validación de entrada con errores por campo |
| 17 | `compraSeRegistraDescuentaStockYApareceSoloEnElHistorialDeSuDueno` | Compra, stock, historial propio y aislamiento de órdenes |
| 18 | `stockInsuficienteYPagoRechazado` | Rechazo de compra sin alterar stock |
| 19 | `adminCambiaEstadoYCancelarDevuelveStock` | Permisos, cambios de estado y devolución de stock |
| 20 | `reportesSoloAdminConDatosReales` | Permisos y cálculos de reportes |
| 21 | `swaggerPublicaLosEndpointsRest` | Especificación OpenAPI disponible y rutas documentadas |

## Resultado de la última ejecución

`mvn test` — **21 pruebas ejecutadas, 0 fallos, 0 errores, 0 omitidas.**

Las pruebas usan H2 en memoria; no demuestran por sí solas la persistencia tras reiniciar una instancia real ni la conexión con MySQL. La persistencia duradera se puede comprobar con el perfil H2 en archivo o una instancia MySQL configurada.
