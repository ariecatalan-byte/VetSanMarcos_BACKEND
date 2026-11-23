package com.vetsanmarcos.dto;

/** Usuario visible por el frontend: NUNCA incluye la contraseña. Los ids viajan como texto. */
public record UsuarioResponse(String id, String nombre, String apellido, String fechaNacimiento, String genero, String correo,
                              String direccion, String region, String comuna, String rol, boolean bloqueado, boolean activo) {}
