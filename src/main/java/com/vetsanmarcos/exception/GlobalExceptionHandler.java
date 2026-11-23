package com.vetsanmarcos.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> responder(HttpStatus status, String mensaje, Map<String, String> errores) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                LocalDateTime.now().toString(), status.value(), status.getReasonPhrase(), mensaje, errores));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> negocio(ApiException e) {
        return responder(e.getStatus(), e.getMessage(), e.getErrores());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException e) {
        Map<String, String> errores = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(f -> errores.putIfAbsent(f.getField(), f.getDefaultMessage()));
        String detalle = errores.isEmpty() ? "" : " " + errores.values().iterator().next();
        return responder(HttpStatus.BAD_REQUEST, "Hay datos inválidos en la solicitud." + detalle, errores);
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponse> cuerpoInvalido(Exception e) {
        return responder(HttpStatus.BAD_REQUEST, "La solicitud tiene un formato inválido.", Map.of());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> prohibido(AccessDeniedException e) {
        return responder(HttpStatus.FORBIDDEN, "No tienes permiso para realizar esta operación.", Map.of());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> noAutenticado(AuthenticationException e) {
        return responder(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión.", Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException e) {
        log.warn("Violación de integridad: {}", e.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT, "La operación viola una restricción de datos (registro duplicado o en uso).", Map.of());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> sinRecurso(NoResourceFoundException e) {
        return responder(HttpStatus.NOT_FOUND, "Recurso no encontrado.", Map.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> inesperado(Exception e) {
        log.error("Error inesperado", e);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error interno. Intenta nuevamente.", Map.of());
    }
}
