package com.vetsanmarcos.controller;

import com.vetsanmarcos.dto.*;
import com.vetsanmarcos.exception.ApiException;
import com.vetsanmarcos.service.AuthService;
import com.vetsanmarcos.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Login, registro, renovación de token y sesión actual")
public class AuthController {

    private static final String COOKIE_REFRESH = "vsm_refresh";
    private static final String RUTA_COOKIE = "/api/v1/auth";

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @Value("${app.jwt.refresh-expiration-days}")
    private long refreshDias;

    @Value("${app.auth.refresh-cookie-secure:false}")
    private boolean cookieSegura;

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión (access token en JSON y refresh token en cookie HttpOnly)")
    public ResponseEntity<SesionResponse> login(@Valid @RequestBody LoginRequest req) {
        return respuestaSesion(authService.login(req));
    }

    @PostMapping("/register")
    @Operation(summary = "Registro público: crea SIEMPRE un cliente y deja la sesión iniciada")
    public ResponseEntity<SesionResponse> register(@Valid @RequestBody UsuarioRequest req) {
        return respuestaSesion(authService.registrar(req), HttpStatus.CREATED);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renovar la sesión (rota el refresh token: el anterior queda inválido)")
    public ResponseEntity<SesionResponse> refresh(
            @CookieValue(name = COOKIE_REFRESH, required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw ApiException.noAutorizado("Sesión inválida. Inicia sesión nuevamente.");
        }
        return respuestaSesion(authService.refrescar(refreshToken));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión: revoca el refresh token")
    public ResponseEntity<Void> logout(
            @CookieValue(name = COOKIE_REFRESH, required = false) String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            authService.cerrarSesion(refreshToken);
        }
        return ResponseEntity.noContent().header(HttpHeaders.SET_COOKIE, cookie("", Duration.ZERO).toString()).build();
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Usuario autenticado (sirve para restaurar la sesión al recargar)")
    public UsuarioResponse me(Authentication auth) {
        return usuarioService.yo(auth.getName());
    }

    private ResponseEntity<SesionResponse> respuestaSesion(AuthResponse sesion) {
        return respuestaSesion(sesion, HttpStatus.OK);
    }

    private ResponseEntity<SesionResponse> respuestaSesion(AuthResponse sesion, HttpStatus status) {
        return ResponseEntity.status(status)
                .header(HttpHeaders.SET_COOKIE, cookie(sesion.refreshToken(), Duration.ofDays(refreshDias)).toString())
                .body(new SesionResponse(sesion.accessToken(), sesion.tokenType(), sesion.expiresIn(), sesion.usuario()));
    }

    private ResponseCookie cookie(String valor, Duration edad) {
        return ResponseCookie.from(COOKIE_REFRESH, valor)
                .httpOnly(true)
                .secure(cookieSegura)
                .sameSite("Strict")
                .path(RUTA_COOKIE)
                .maxAge(edad)
                .build();
    }

    public record SesionResponse(String accessToken, String tokenType, long expiresIn, UsuarioResponse usuario) {}
}
