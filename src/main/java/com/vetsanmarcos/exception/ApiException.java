package com.vetsanmarcos.exception;

import org.springframework.http.HttpStatus;

import java.util.Map;

/** Error de negocio con código HTTP y mensaje para el usuario. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final Map<String, String> errores;

    public ApiException(HttpStatus status, String mensaje) {
        this(status, mensaje, Map.of());
    }

    public ApiException(HttpStatus status, String mensaje, Map<String, String> errores) {
        super(mensaje);
        this.status = status;
        this.errores = errores;
    }

    public HttpStatus getStatus() { return status; }
    public Map<String, String> getErrores() { return errores; }

    public static ApiException noEncontrado(String mensaje) { return new ApiException(HttpStatus.NOT_FOUND, mensaje); }
    public static ApiException conflicto(String mensaje) { return new ApiException(HttpStatus.CONFLICT, mensaje); }
    public static ApiException invalida(String mensaje) { return new ApiException(HttpStatus.BAD_REQUEST, mensaje); }
    public static ApiException invalida(String campo, String mensaje) {
        return new ApiException(HttpStatus.BAD_REQUEST, mensaje, Map.of(campo, mensaje));
    }
    public static ApiException prohibido(String mensaje) { return new ApiException(HttpStatus.FORBIDDEN, mensaje); }
    public static ApiException noAutorizado(String mensaje) { return new ApiException(HttpStatus.UNAUTHORIZED, mensaje); }
}
