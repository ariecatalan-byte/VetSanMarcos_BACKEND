package com.vetsanmarcos.dto;

/** Respuesta de login / registro / refresh. expiresIn = segundos de vida del access token. */
public record AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresIn, UsuarioResponse usuario) {}
