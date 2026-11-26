package com.vetsanmarcos;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración (frontend ⇄ API ⇄ base de datos): usan el contexto completo de Spring,
 * la capa de seguridad real (JWT, roles) y una base H2 con los datos de prueba del DataLoader.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    // ----------------------------- utilidades -----------------------------
    private ResultActions enviar(String url, String token, Object body) throws Exception {
        var req = post(url).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(body));
        if (token != null) req.header("Authorization", "Bearer " + token);
        return mvc.perform(req);
    }

    private ResultActions leer(String url, String token) throws Exception {
        var req = get(url);
        if (token != null) req.header("Authorization", "Bearer " + token);
        return mvc.perform(req);
    }

    private JsonNode login(String correo, String password) throws Exception {
        String json = enviar("/api/v1/auth/login", null, Map.of("correo", correo, "password", password))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return om.readTree(json);
    }

    private String token(String correo, String password) throws Exception {
        return login(correo, password).get("accessToken").asText();
    }

    private ResultActions renovar(Cookie cookie) throws Exception {
        return mvc.perform(post("/api/v1/auth/refresh").cookie(cookie));
    }

    private Map<String, Object> datosUsuario(String correo) {
        return Map.ofEntries(
                Map.entry("nombre", "Test"), Map.entry("apellido", "Usuario"), Map.entry("fechaNacimiento", "2000-01-01"),
                Map.entry("genero", "Otro"), Map.entry("correo", correo), Map.entry("password", "Clave1234"),
                Map.entry("direccion", "Calle 1"), Map.entry("region", "Región Metropolitana de Santiago"),
                Map.entry("comuna", "Santiago"), Map.entry("rol", "administrador"));
    }

    private Map<String, Object> ordenDe(String idProducto, int cantidad, boolean aprobado) {
        return Map.of(
                "lineas", List.of(Map.of("idProducto", idProducto, "cantidad", cantidad)),
                "cliente", Map.of("nombre", "Matías", "apellidos", "Soto", "correo", "cliente@duoc.cl"),
                "entrega", Map.of("calle", "Pasaje Las Rosas 789", "departamento", "", "region", "Región Metropolitana de Santiago",
                        "comuna", "Puente Alto", "indicaciones", ""),
                "pagoAprobado", aprobado);
    }

    // ----------------------------- catálogo público -----------------------------
    @Test
    void catalogoEsPublicoYDevuelveJson() throws Exception {
        leer("/api/v1/productos", null).andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(12)))
                .andExpect(jsonPath("$[0].nombre").exists());
        leer("/api/v1/categorias", null).andExpect(status().isOk()).andExpect(jsonPath("$.length()", greaterThanOrEqualTo(5)));
        leer("/api/v1/productos/999999", null).andExpect(status().isNotFound());
    }

    // ----------------------------- login -----------------------------
    @Test
    void loginCorrectoEntregaAccessTokenYCookieHttpOnly() throws Exception {
        var respuesta = enviar("/api/v1/auth/login", null, Map.of("correo", "admin@duoc.cl", "password", "Admin1234"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").doesNotExist())
                .andExpect(jsonPath("$.usuario.rol").value("administrador"))
                .andExpect(jsonPath("$.usuario.password").doesNotExist())
                .andReturn();
        Cookie refresh = respuesta.getResponse().getCookie("vsm_refresh");
        org.junit.jupiter.api.Assertions.assertNotNull(refresh);
        org.junit.jupiter.api.Assertions.assertTrue(respuesta.getResponse().getHeader("Set-Cookie").contains("HttpOnly"));
        org.junit.jupiter.api.Assertions.assertTrue(respuesta.getResponse().getHeader("Set-Cookie").contains("SameSite=Strict"));
        org.junit.jupiter.api.Assertions.assertTrue(respuesta.getResponse().getHeader("Set-Cookie").contains("Path=/api/v1/auth"));
    }

    @Test
    void loginConCredencialesIncorrectasDa401() throws Exception {
        enviar("/api/v1/auth/login", null, Map.of("correo", "admin@duoc.cl", "password", "mala-clave"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").exists());
        enviar("/api/v1/auth/login", null, Map.of("correo", "noexiste@duoc.cl", "password", "Clave1234"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cuentaSeBloqueaTrasTresIntentosFallidos() throws Exception {
        String correo = "bloqueo" + UUID.randomUUID().toString().substring(0, 6) + "@duoc.cl";
        enviar("/api/v1/auth/register", null, datosUsuario(correo)).andExpect(status().isCreated());
        for (int i = 0; i < 2; i++) {
            enviar("/api/v1/auth/login", null, Map.of("correo", correo, "password", "incorrecta1")).andExpect(status().isUnauthorized());
        }
        enviar("/api/v1/auth/login", null, Map.of("correo", correo, "password", "incorrecta1")).andExpect(status().isLocked());
        // aunque ahora use la clave correcta, sigue bloqueada
        enviar("/api/v1/auth/login", null, Map.of("correo", correo, "password", "Clave1234")).andExpect(status().isLocked());
    }

    @Test
    void registroPublicoSiempreCreaCliente() throws Exception {
        String correo = "nuevo" + UUID.randomUUID().toString().substring(0, 6) + "@duoc.cl";
        enviar("/api/v1/auth/register", null, datosUsuario(correo)) // el body intenta pedir rol "administrador"
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.usuario.rol").value("cliente"));
        enviar("/api/v1/auth/register", null, datosUsuario(correo)).andExpect(status().isConflict()); // correo repetido
    }

    @Test
    void registroValidaDominioEdadYPassword() throws Exception {
        Map<String, Object> dominio = new java.util.HashMap<>(datosUsuario("alguien@gmail.com"));
        enviar("/api/v1/auth/register", null, dominio).andExpect(status().isBadRequest());
        Map<String, Object> pass = new java.util.HashMap<>(datosUsuario("pass" + UUID.randomUUID().toString().substring(0, 6) + "@duoc.cl"));
        pass.put("password", "sololetras");
        enviar("/api/v1/auth/register", null, pass).andExpect(status().isBadRequest());
        Map<String, Object> edad = new java.util.HashMap<>(datosUsuario("edad" + UUID.randomUUID().toString().substring(0, 6) + "@duoc.cl"));
        edad.put("fechaNacimiento", java.time.LocalDate.now().minusYears(10).toString());
        enviar("/api/v1/auth/register", null, edad).andExpect(status().isBadRequest());
    }

    // ----------------------------- token / sesión -----------------------------
    @Test
    void endpointProtegidoSinTokenOTokenInvalidoDa401() throws Exception {
        leer("/api/v1/ordenes/mis", null).andExpect(status().isUnauthorized());
        leer("/api/v1/auth/me", "esto.no.es.un.jwt").andExpect(status().isUnauthorized());
    }

    @Test
    void meDevuelveElUsuarioDelToken() throws Exception {
        String t = token("cliente@duoc.cl", "Cliente1234");
        leer("/api/v1/auth/me", t).andExpect(status().isOk()).andExpect(jsonPath("$.correo").value("cliente@duoc.cl"));
    }

    @Test
    void refreshRotaElTokenYElAnteriorQuedaInvalido() throws Exception {
        var login = enviar("/api/v1/auth/login", null, Map.of("correo", "vendedor@duoc.cl", "password", "Vendedor1234"))
                .andExpect(status().isOk()).andReturn();
        Cookie refresh = login.getResponse().getCookie("vsm_refresh");
        org.junit.jupiter.api.Assertions.assertNotNull(refresh);
        var renovado = renovar(refresh).andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").doesNotExist()).andReturn();
        Cookie nuevo = renovado.getResponse().getCookie("vsm_refresh");
        org.junit.jupiter.api.Assertions.assertNotNull(nuevo);
        // reutilizar el refresh anterior debe fallar
        renovar(refresh).andExpect(status().isUnauthorized());
        // y detecta reutilización: se revocan también los demás
        renovar(nuevo).andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRevocaElRefreshToken() throws Exception {
        var login = enviar("/api/v1/auth/login", null, Map.of("correo", "cliente@duoc.cl", "password", "Cliente1234"))
                .andExpect(status().isOk()).andReturn();
        Cookie refresh = login.getResponse().getCookie("vsm_refresh");
        org.junit.jupiter.api.Assertions.assertNotNull(refresh);
        mvc.perform(post("/api/v1/auth/logout").cookie(refresh))
                .andExpect(status().isNoContent())
                .andExpect(header().string("Set-Cookie", containsString("Max-Age=0")));
        renovar(refresh).andExpect(status().isUnauthorized());
    }

    @Test
    void refreshSinCookieDa401() throws Exception {
        mvc.perform(post("/api/v1/auth/refresh")).andExpect(status().isUnauthorized());
    }

    // ----------------------------- roles -----------------------------
    @Test
    void soloAdminListaUsuarios() throws Exception {
        leer("/api/v1/usuarios", token("cliente@duoc.cl", "Cliente1234")).andExpect(status().isForbidden());
        leer("/api/v1/usuarios", token("vendedor@duoc.cl", "Vendedor1234")).andExpect(status().isForbidden());
        leer("/api/v1/usuarios", token("admin@duoc.cl", "Admin1234")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].password").doesNotExist());
        leer("/api/v1/usuarios", null).andExpect(status().isUnauthorized());
    }

    @Test
    void vendedorSoloModificaStockYSoloCreaClientes() throws Exception {
        String vendedor = token("vendedor@duoc.cl", "Vendedor1234");
        String cliente = token("cliente@duoc.cl", "Cliente1234");
        mvc.perform(patch("/api/v1/productos/2/stock").header("Authorization", "Bearer " + vendedor)
                .contentType(MediaType.APPLICATION_JSON).content("{\"stock\":33}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.stock").value(33));
        mvc.perform(patch("/api/v1/productos/2/stock").header("Authorization", "Bearer " + cliente)
                .contentType(MediaType.APPLICATION_JSON).content("{\"stock\":1}")).andExpect(status().isForbidden());
        mvc.perform(delete("/api/v1/productos/2").header("Authorization", "Bearer " + vendedor)).andExpect(status().isForbidden());
        // vendedor crea usuario pidiendo rol admin → 403; como cliente → 201
        enviar("/api/v1/usuarios", vendedor, datosUsuario("v" + UUID.randomUUID().toString().substring(0, 6) + "@duoc.cl"))
                .andExpect(status().isForbidden());
        Map<String, Object> comoCliente = new java.util.HashMap<>(datosUsuario("c" + UUID.randomUUID().toString().substring(0, 6) + "@duoc.cl"));
        comoCliente.put("rol", "cliente");
        enviar("/api/v1/usuarios", vendedor, comoCliente).andExpect(status().isCreated());
    }

    // ----------------------------- CRUD -----------------------------
    @Test
    void crudDeProductosYCategoriasComoAdmin() throws Exception {
        String admin = token("admin@duoc.cl", "Admin1234");
        // categoría
        String cat = enviar("/api/v1/categorias", admin, Map.of("nombre", "Pruebas" + UUID.randomUUID().toString().substring(0, 4), "descripcion", "x"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String catId = om.readTree(cat).get("id").asText();
        // producto en esa categoría
        Map<String, Object> producto = Map.of("nombre", "Producto prueba", "categoriaId", catId, "especie", "Perro", "precio", 5000,
                "descuento", 10, "stock", 7, "stockCritico", 2, "descripcion", "desc", "imagen", "");
        String creado = enviar("/api/v1/productos", admin, producto).andExpect(status().isCreated())
                .andExpect(jsonPath("$.precio").value(5000)).andReturn().getResponse().getContentAsString();
        String prodId = om.readTree(creado).get("id").asText();
        // la categoría con productos no se puede eliminar
        mvc.perform(delete("/api/v1/categorias/" + catId).header("Authorization", "Bearer " + admin)).andExpect(status().isConflict());
        // actualizar (PUT)
        Map<String, Object> editado = new java.util.HashMap<>(producto);
        editado.put("precio", 6000);
        mvc.perform(put("/api/v1/productos/" + prodId).header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(editado)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.precio").value(6000));
        // eliminar
        mvc.perform(delete("/api/v1/productos/" + prodId).header("Authorization", "Bearer " + admin)).andExpect(status().isNoContent());
        leer("/api/v1/productos/" + prodId, null).andExpect(status().isNotFound());
        mvc.perform(delete("/api/v1/categorias/" + catId).header("Authorization", "Bearer " + admin)).andExpect(status().isNoContent());
    }

    @Test
    void adminGestionaUsuariosYDesactivaQuienesTienenCompras() throws Exception {
        String admin = token("admin@duoc.cl", "Admin1234");

        String correoAdminCreado = "admin" + UUID.randomUUID().toString().substring(0, 6) + "@duoc.cl";
        Map<String, Object> nuevoAdmin = datosUsuario(correoAdminCreado);
        String usuarioJson = enviar("/api/v1/usuarios", admin, nuevoAdmin).andExpect(status().isCreated())
                .andExpect(jsonPath("$.rol").value("administrador")).andReturn().getResponse().getContentAsString();
        String idAdminCreado = om.readTree(usuarioJson).get("id").asText();

        Map<String, Object> editar = new java.util.HashMap<>(nuevoAdmin);
        editar.put("nombre", "Usuario editado");
        editar.put("rol", "vendedor");
        mvc.perform(put("/api/v1/usuarios/" + idAdminCreado).header("Authorization", "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsString(editar)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.nombre").value("Usuario editado"))
                .andExpect(jsonPath("$.rol").value("vendedor"));
        mvc.perform(delete("/api/v1/usuarios/" + idAdminCreado).header("Authorization", "Bearer " + admin))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/usuarios/" + idAdminCreado).header("Authorization", "Bearer " + admin))
                .andExpect(status().isNotFound());

        String correoCliente = "compra" + UUID.randomUUID().toString().substring(0, 6) + "@duoc.cl";
        String registro = enviar("/api/v1/auth/register", null, datosUsuario(correoCliente))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        JsonNode cliente = om.readTree(registro).get("usuario");
        String idCliente = cliente.get("id").asText();
        String tokenCliente = om.readTree(registro).get("accessToken").asText();
        enviar("/api/v1/ordenes", tokenCliente, ordenDe("1", 1, true)).andExpect(status().isCreated());

        mvc.perform(delete("/api/v1/usuarios/" + idCliente).header("Authorization", "Bearer " + admin))
                .andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/usuarios/" + idCliente).header("Authorization", "Bearer " + admin))
                .andExpect(status().isOk()).andExpect(jsonPath("$.activo").value(false));
    }

    @Test
    void datosInvalidosDan400ConDetallePorCampo() throws Exception {
        String admin = token("admin@duoc.cl", "Admin1234");
        Map<String, Object> malo = Map.of("nombre", "", "categoriaId", "1", "especie", "Perro", "precio", -5,
                "descuento", 99, "stock", 1, "stockCritico", 1, "descripcion", "d", "imagen", "");
        enviar("/api/v1/productos", admin, malo).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.precio").exists())
                .andExpect(jsonPath("$.errores.descuento").exists());
    }

    // ----------------------------- compra → historial -----------------------------
    @Test
    void compraSeRegistraDescuentaStockYApareceSoloEnElHistorialDeSuDueno() throws Exception {
        String cliente = token("cliente@duoc.cl", "Cliente1234");
        String otro = token("camila.fuentes@duoc.cl", "Cliente1234");
        String admin = token("admin@duoc.cl", "Admin1234");

        int stockAntes = om.readTree(leer("/api/v1/productos/1", null).andReturn().getResponse().getContentAsString()).get("stock").asInt();

        String json = enviar("/api/v1/ordenes", cliente, ordenDe("1", 2, true)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("pagada"))
                .andExpect(jsonPath("$.total").value(8400)) // 2 × $4.200 calculado por el servidor
                .andReturn().getResponse().getContentAsString();
        String id = om.readTree(json).get("id").asText();

        int stockDespues = om.readTree(leer("/api/v1/productos/1", null).andReturn().getResponse().getContentAsString()).get("stock").asInt();
        org.junit.jupiter.api.Assertions.assertEquals(stockAntes - 2, stockDespues);

        leer("/api/v1/ordenes/mis", cliente).andExpect(status().isOk()).andExpect(jsonPath("$[*].id", hasItem(id)));
        leer("/api/v1/ordenes/mis", otro).andExpect(status().isOk()).andExpect(jsonPath("$[*].id", not(hasItem(id))));
        leer("/api/v1/ordenes/" + id, cliente).andExpect(status().isOk());
        leer("/api/v1/ordenes/" + id, otro).andExpect(status().isForbidden());   // no puede ver la de otro usuario
        leer("/api/v1/ordenes/" + id, admin).andExpect(status().isOk());
        leer("/api/v1/ordenes", cliente).andExpect(status().isForbidden());      // listado global solo admin
        leer("/api/v1/ordenes", admin).andExpect(status().isOk());
    }

    @Test
    void stockInsuficienteYPagoRechazado() throws Exception {
        String cliente = token("cliente@duoc.cl", "Cliente1234");
        enviar("/api/v1/ordenes", cliente, ordenDe("3", 99999, true)).andExpect(status().isConflict());
        enviar("/api/v1/ordenes", null, ordenDe("3", 1, true)).andExpect(status().isUnauthorized());

        int antes = om.readTree(leer("/api/v1/productos/3", null).andReturn().getResponse().getContentAsString()).get("stock").asInt();
        enviar("/api/v1/ordenes", cliente, ordenDe("3", 1, false)).andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("rechazada"));
        int despues = om.readTree(leer("/api/v1/productos/3", null).andReturn().getResponse().getContentAsString()).get("stock").asInt();
        org.junit.jupiter.api.Assertions.assertEquals(antes, despues); // pago rechazado no toca el stock
    }

    @Test
    void adminCambiaEstadoYCancelarDevuelveStock() throws Exception {
        String cliente = token("cliente@duoc.cl", "Cliente1234");
        String admin = token("admin@duoc.cl", "Admin1234");
        int antes = om.readTree(leer("/api/v1/productos/4", null).andReturn().getResponse().getContentAsString()).get("stock").asInt();
        String id = om.readTree(enviar("/api/v1/ordenes", cliente, ordenDe("4", 3, true)).andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString()).get("id").asText();

        mvc.perform(patch("/api/v1/ordenes/" + id + "/estado").header("Authorization", "Bearer " + cliente)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"cancelada\"}")).andExpect(status().isForbidden());
        mvc.perform(patch("/api/v1/ordenes/" + id + "/estado").header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"entregada\"}")).andExpect(status().isConflict()); // salto inválido
        mvc.perform(patch("/api/v1/ordenes/" + id + "/estado").header("Authorization", "Bearer " + admin)
                .contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"cancelada\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.estado").value("cancelada"));

        int despues = om.readTree(leer("/api/v1/productos/4", null).andReturn().getResponse().getContentAsString()).get("stock").asInt();
        org.junit.jupiter.api.Assertions.assertEquals(antes, despues);
    }

    // ----------------------------- reportes -----------------------------
    @Test
    void reportesSoloAdminConDatosReales() throws Exception {
        leer("/api/v1/reportes/resumen?periodo=anio", token("cliente@duoc.cl", "Cliente1234")).andExpect(status().isForbidden());
        leer("/api/v1/reportes/resumen?periodo=anio", token("admin@duoc.cl", "Admin1234")).andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOperaciones", greaterThan(0)))
                .andExpect(jsonPath("$.montoTotal", greaterThan(0)))
                .andExpect(jsonPath("$.topProductos.length()", greaterThan(0)))
                .andExpect(jsonPath("$.serie.length()", greaterThan(0)));
        leer("/api/v1/reportes/resumen?periodo=semana", token("admin@duoc.cl", "Admin1234")).andExpect(status().isOk());
        leer("/api/v1/reportes/resumen?periodo=invalido", token("admin@duoc.cl", "Admin1234")).andExpect(status().isBadRequest());
    }

    @Test
    void swaggerPublicaLosEndpointsRest() throws Exception {
        mvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/api/v1/productos'].get").exists())
                .andExpect(jsonPath("$.paths['/api/v1/ordenes'].post").exists())
                .andExpect(jsonPath("$.paths['/api/v1/usuarios'].delete").doesNotExist());
    }
}
