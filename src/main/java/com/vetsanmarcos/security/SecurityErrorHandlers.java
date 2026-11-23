package com.vetsanmarcos.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetsanmarcos.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/** 401 (sin token válido) y 403 (sin permiso) siempre en formato JSON. */
@Component
@RequiredArgsConstructor
public class SecurityErrorHandlers implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ObjectMapper mapper;

    private void escribir(HttpServletResponse resp, HttpStatus status, String mensaje) throws IOException {
        resp.setStatus(status.value());
        resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), new ErrorResponse(LocalDateTime.now().toString(), status.value(),
                status.getReasonPhrase(), mensaje, Map.of()));
    }

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException {
        escribir(resp, HttpStatus.UNAUTHORIZED, "Debes iniciar sesión (token ausente, inválido o expirado).");
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse resp, AccessDeniedException e) throws IOException {
        escribir(resp, HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta operación.");
    }
}
