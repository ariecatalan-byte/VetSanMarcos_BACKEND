package com.vetsanmarcos.controller;

import com.vetsanmarcos.dto.*;
import com.vetsanmarcos.service.AuthService;
import com.vetsanmarcos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login, registro, renovación de token y sesión actual")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión (devuelve access token JWT + refresh token)")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registro público: crea SIEMPRE un cliente y deja la sesión iniciada")
    public AuthResponse register(@Valid @RequestBody UsuarioRequest req) {
        return authService.registrar(req);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar la sesión (rota el refresh token: el anterior queda inválido)")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest req) {
        return authService.refrescar(req.refreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Cerrar sesión: revoca el refresh token")
    public void logout(@Valid @RequestBody RefreshRequest req) {
        authService.cerrarSesion(req.refreshToken());
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Usuario autenticado (sirve para restaurar la sesión al recargar)")
    public UsuarioResponse me(Authentication auth) {
        return usuarioService.yo(auth.getName());
    }
}
