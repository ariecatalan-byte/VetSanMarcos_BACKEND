package com.vetsanmarcos.exception;

import java.util.Map;

/** Formato único de error JSON de toda la API. */
public record ErrorResponse(String timestamp, int status, String error, String mensaje, Map<String, String> errores) {}
