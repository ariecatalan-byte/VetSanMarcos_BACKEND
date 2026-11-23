package com.vetsanmarcos.entity;

/** Roles del sistema. El frontend los maneja en minúsculas ("administrador", "vendedor", "cliente"). */
public enum Rol {
    ADMIN, VENDEDOR, CLIENTE;

    public String aFrontend() {
        return switch (this) {
            case ADMIN -> "administrador";
            case VENDEDOR -> "vendedor";
            case CLIENTE -> "cliente";
        };
    }

    public static Rol desdeFrontend(String valor) {
        if (valor == null) return null;
        return switch (valor.trim().toLowerCase()) {
            case "administrador", "admin" -> ADMIN;
            case "vendedor" -> VENDEDOR;
            case "cliente" -> CLIENTE;
            default -> null;
        };
    }
}
