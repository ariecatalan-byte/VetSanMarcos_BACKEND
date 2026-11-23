# Pruebas de integración del backend

Clase: `src/test/java/com/vetsanmarcos/ApiIntegrationTest.java` — contexto Spring completo, seguridad real (JWT y roles),
H2 en memoria con los datos de prueba del `DataLoader`. Ejecutar con `mvn test`.

| # | Método de prueba | Verifica |
|---|---|---|
| 1 | `catalogoEsPublicoYDevuelveJson` | GET productos/categorías sin token → 200 JSON; id inexistente → 404 |
| 2 | `loginCorrectoEntregaTokensYUsuarioSinPassword` | 200, access + refresh token, rol, la respuesta no contiene contraseña |
| 3 | `loginConCredencialesIncorrectasDa401` | Clave errónea / usuario inexistente → 401 |
| 4 | `cuentaSeBloqueaTrasTresIntentosFallidos` | 401, 401, 423 y sigue bloqueada con clave correcta |
| 5 | `registroPublicoSiempreCreaCliente` | Aunque pida rol admin → cliente; correo repetido → 409 |
| 6 | `registroValidaDominioEdadYPassword` | Dominio ≠ @duoc.cl, contraseña débil, menor de 14 → 400 |
| 7 | `endpointProtegidoSinTokenOTokenInvalidoDa401` | Sin token / token falso → 401 |
| 8 | `meDevuelveElUsuarioDelToken` | `/auth/me` con token válido |
| 9 | `refreshRotaElTokenYElAnteriorQuedaInvalido` | Renovación OK; reutilizar el anterior → 401 y se revocan los demás |
| 10 | `logoutRevocaElRefreshToken` | Logout 204; el refresh ya no sirve |
| 11 | `soloAdminListaUsuarios` | cliente/vendedor 403, admin 200, sin token 401 |
| 12 | `vendedorSoloModificaStockYSoloCreaClientes` | PATCH stock vendedor 200 / cliente 403; vendedor no elimina ni crea admins |
| 13 | `crudDeProductosYCategoriasComoAdmin` | POST 201, PUT 200, DELETE 204, categoría con productos → 409 |
| 14 | `datosInvalidosDan400ConDetallePorCampo` | 400 con `errores` por campo |
| 15 | `compraSeRegistraDescuentaStockYApareceSoloEnElHistorialDeSuDueno` | Total calculado por el servidor, stock descontado, historial propio, orden ajena → 403, admin → 200 |
| 16 | `stockInsuficienteYPagoRechazado` | 409 sin stock; 401 sin token; pago rechazado no toca el stock |
| 17 | `adminCambiaEstadoYCancelarDevuelveStock` | Cliente 403; salto inválido 409; cancelar devuelve stock |
| 18 | `reportesSoloAdminConDatosReales` | Cliente 403; admin con cifras > 0 para semana/año; periodo inválido 400 |

## Resultado de la última ejecución

_Pegar aquí la salida de `mvn test` (BUILD SUCCESS, "Tests run: 18, Failures: 0")._
