package com.vetsanmarcos.controller;

import com.vetsanmarcos.dto.PerfilRequest;
import com.vetsanmarcos.dto.UsuarioRequest;
import com.vetsanmarcos.dto.UsuarioResponse;
import com.vetsanmarcos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Usuarios", description = "Gestión de usuarios y roles (panel administrativo) y perfil propio")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar usuarios registrados (ADMIN)")
    public List<UsuarioResponse> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Detalle de un usuario (ADMIN)")
    public UsuarioResponse obtener(@PathVariable Long id) {
        return usuarioService.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN','VENDEDOR')")
    @Operation(summary = "Crear usuario (ADMIN: cualquier rol; VENDEDOR: solo clientes)")
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest req, Authentication auth) {
        return usuarioService.crear(req, auth.getName());
    }

    @PutMapping("/me")
    @Operation(summary = "Editar mi propio perfil (sin rol ni contraseña)")
    public UsuarioResponse actualizarPerfil(@Valid @RequestBody PerfilRequest req, Authentication auth) {
        return usuarioService.actualizarPerfil(req, auth.getName());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Editar usuario y su rol (ADMIN)")
    public UsuarioResponse actualizar(@PathVariable Long id, @Valid @RequestBody UsuarioRequest req, Authentication auth) {
        return usuarioService.actualizar(id, req, auth.getName());
    }

    @PatchMapping("/{id}/desbloquear")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desbloquear una cuenta bloqueada por intentos fallidos (ADMIN)")
    public UsuarioResponse desbloquear(@PathVariable Long id) {
        return usuarioService.desbloquear(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Eliminar usuario; si tiene compras se desactiva (ADMIN)")
    public void eliminar(@PathVariable Long id, Authentication auth) {
        usuarioService.eliminar(id, auth.getName());
    }
}
